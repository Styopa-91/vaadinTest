package com.project.vaadintest.components;

import com.project.vaadintest.domain.Employee;
import com.project.vaadintest.repo.EmployeeRepo;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class EmployeeEditor extends VerticalLayout implements KeyNotifier {
    @Autowired
    private EmployeeRepo employeeRepo;

    private Employee employee;

    @Setter
    private ChangeHandler changeHandler;

    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete", VaadinIcon.TRASH.create());

    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField patronymic = new TextField("Patronymic");
    private RichTextEditor description = new RichTextEditor();

    private DatePicker datePicker = new DatePicker("Date of birth");

    private Binder<Employee> binder = new Binder<>(Employee.class);
    @Setter
    private Dialog dialog;
//    RichTextEditor editor = new RichTextEditor();

    public EmployeeEditor() {
        this.firstName.setWidth("100%");
        this.lastName.setWidth("100%");
        this.patronymic.setWidth("100%");
        this.description.setWidth("100%");
        this.datePicker.setWidth("100%");
        description.setValue("<p>Write your <strong>formatted</strong> text here...</p>");

        HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

        add(lastName, firstName, patronymic, description, datePicker, actions);
        binder.bindInstanceFields(this);
        setPadding(false);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> {
            setVisible(false);
            if (dialog != null) {
                dialog.close(); // Close the dialog when cancel is clicked
            }
        });
        setVisible(false);
    }

    public void editEmployee(Employee newEmployee) {
        if (newEmployee == null) {
            setVisible(false);
            return;
        }
        if (newEmployee.getId() != null) {
            employee = employeeRepo.findById(newEmployee.getId()).orElse(newEmployee);
        } else {
            employee = newEmployee;
        }
        binder.forField(datePicker)
                .bind(Employee::getBirthDate, Employee::setBirthDate);

        binder.forField(description)
                .bind(Employee::getDescription, Employee::setDescription);

        binder.setBean(employee);

        setVisible(true);

        lastName.focus();
    }

    private void delete() {
        employeeRepo.delete(employee);
        changeHandler.onChange();
    }

    private void save() {
        employee.setBirthDate(datePicker.getValue());
        employee.setDescription(description.getValue());
        employeeRepo.save(employee);
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

}
