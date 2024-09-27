package com.project.vaadintest.components;

import com.project.vaadintest.domain.Department;
import com.project.vaadintest.repo.DepartmentRepo;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.wontlost.ckeditor.Constants;
import com.wontlost.ckeditor.VaadinCKEditor;
import com.wontlost.ckeditor.VaadinCKEditorBuilder;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class DepartmentEditor extends VerticalLayout implements KeyNotifier {
    @Autowired
    private DepartmentRepo departmentRepo;

    private Department department;

    @Setter
    private ChangeHandler changeHandler;

    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private TextField title = new TextField("Title");
//    private RichTextEditor description = new RichTextEditor();
    private VaadinCKEditor description =new VaadinCKEditorBuilder().with(builder -> {
        builder.editorData = "<p>This is a classic editor sample.</p>";
        builder.editorType = Constants.EditorType.CLASSIC;
        builder.theme = Constants.ThemeType.DARK;
    }).createVaadinCKEditor();

    private Binder<Department> binder = new Binder<>(Department.class);
    @Setter
    private Dialog dialog;

    public DepartmentEditor() {
//        this.description.setWidth("100%");
//        description.setHeight("400px");
        description.setValue("<p>Write your <strong>formatted</strong> text here...</p>");

        HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

        add(title, description, actions);
        binder.bindInstanceFields(this);
        setPadding(false);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> {
            setVisible(false);
            if (dialog != null) {
                dialog.close(); // Close the dialog when cancel is clicked
                changeHandler.onChange();
            }
        });
        setVisible(false);
    }

    public void editDepartment(Department newDepartment) {
        if (newDepartment == null) {
            setVisible(false);
            return;
        }
        if (newDepartment.getId() != null) {
            department = departmentRepo.findById(newDepartment.getId()).orElse(newDepartment);
        } else {
            department = newDepartment;
        }
        binder.forField(description)
                .bind(Department::getDescription, Department::setDescription);
        binder.setBean(department);
        setVisible(true);
        title.focus();
    }

    private void delete() {
        departmentRepo.delete(department);
        changeHandler.onChange();
    }

    private void save() {
        department.setTitle(title.getValue());
        department.setDescription(description.getValue());
        departmentRepo.save(department);
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }
}
