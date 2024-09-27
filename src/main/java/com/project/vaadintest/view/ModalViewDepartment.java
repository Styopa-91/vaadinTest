package com.project.vaadintest.view;

import com.project.vaadintest.components.DepartmentEditor;
import com.project.vaadintest.components.EmployeeEditor;
import com.project.vaadintest.domain.Department;
import com.project.vaadintest.domain.Employee;
import com.project.vaadintest.repo.DepartmentRepo;
import com.project.vaadintest.repo.EmployeeRepo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.stream.Stream;

@Route("departments")
public class ModalViewDepartment extends VerticalLayout {

    private final DepartmentRepo departmentRepo;

    private final Grid<Department> grid = new Grid<>(Department.class);

    private final Dialog editorDialog = new Dialog();  // Dialog for editor

    @Autowired
    public ModalViewDepartment(DepartmentRepo departmentRepo, DepartmentEditor editor) {
        this.departmentRepo = departmentRepo;

        Button backButton = new Button("Back to Main View", event ->
                getUI().ifPresent(ui -> ui.navigate(MainView.class)));
        add(backButton);
        Button addNewBtn = new Button("Add new");
        HorizontalLayout toolbar = new HorizontalLayout(backButton, addNewBtn);
        grid.setColumns("id", "title", "description");
        grid.setWidth("100%");

        editor.setWidth("auto");
        editorDialog.setWidth("100%");

        editor.setChangeHandler(() -> {
            editorDialog.close(); // Close the dialog when changes are saved
            showEmployee();
//            getUI().ifPresent(ui -> ui.getPage().reload());
        });

        editor.setDialog(editorDialog);

        // Add editor to dialog
        editorDialog.add(editor);
        editorDialog.setModal(true);

        // Listen for dialog close events
        editorDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                // The dialog is now closed
                editor.setVisible(false); // Optionally hide the editor
                showEmployee(); // Refresh the grid or any other logic
            }
        });

        // Connect selected Employee to editor dialog or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                editor.editDepartment(e.getValue());
                editorDialog.open();  // Open the dialog when an employee is selected
            }
        });

        // Instantiate and edit new Employee when the new button is clicked
        addNewBtn.addClickListener(e -> {
            editor.editDepartment(new Department());
            editorDialog.open();  // Open the dialog for new employee
        });

        // Set up layout
        HorizontalLayout main = new HorizontalLayout(grid);
        main.setSizeFull();
        add(toolbar, main);
        setSizeFull();
        showEmployee();
    }

    private void showEmployee() {
        CallbackDataProvider<Department, Void> dataProvider = new CallbackDataProvider<>(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    if (limit == 0) {
                        return Stream.empty();
                    }
                    Pageable pageable = PageRequest.of(offset / limit, limit);

                    return departmentRepo.findAll(pageable).stream(); // Fetch all employees

                },
                query -> {
                    // Return the total count based on whether name filter is applied
                    return (int) departmentRepo.count(); // Total number of employees
                }
        );

        grid.setDataProvider(dataProvider);
    }
}
