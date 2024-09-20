package com.project.vaadintest.view;

import com.project.vaadintest.components.EmployeeEditor;
import com.project.vaadintest.domain.Employee;
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

@Route("modal-view")  // Different route to distinguish from the MainView
public class ModalView extends VerticalLayout {

    private final EmployeeRepo employeeRepo;

    private final Grid<Employee> grid = new Grid<>(Employee.class);

    private final TextField filter = new TextField("", "Type to filter");

    private final Dialog editorDialog = new Dialog();  // Dialog for editor

    @Autowired
    public ModalView(EmployeeRepo employeeRepo, EmployeeEditor editor) {
        this.employeeRepo = employeeRepo;

        Button backButton = new Button("Back to Main View", event ->
                getUI().ifPresent(ui -> ui.navigate(MainView.class)));
        add(backButton);
        Button addNewBtn = new Button("Add new");
        HorizontalLayout toolbar = new HorizontalLayout(backButton, filter, addNewBtn);
        grid.setColumns("id", "lastName", "firstName", "patronymic", "description", "birthDate");
        grid.setWidth("100%");

        editor.setWidth("auto");
        editor.setChangeHandler(() -> {
            editorDialog.close(); // Close the dialog when changes are saved
            showEmployee(filter.getValue());
        });

        editor.setDialog(editorDialog);

        // Add editor to dialog
        editorDialog.add(editor);
        editorDialog.setModal(true);

        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> showEmployee(e.getValue()));

        // Connect selected Employee to editor dialog or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                editor.editEmployee(e.getValue());
                editorDialog.open();  // Open the dialog when an employee is selected
            }
        });

        // Instantiate and edit new Employee when the new button is clicked
        addNewBtn.addClickListener(e -> {
            editor.editEmployee(new Employee());
            editorDialog.open();  // Open the dialog for new employee
        });

        // Set up layout
        HorizontalLayout main = new HorizontalLayout(grid);
        main.setSizeFull();
        add(toolbar, main);
        setSizeFull();
        showEmployee("");
    }

    private void showEmployee(String name) {
        CallbackDataProvider<Employee, Void> dataProvider = new CallbackDataProvider<>(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    // Ensure the limit is not zero to avoid division by zero errors
                    if (limit == 0) {
                        return Stream.empty();
                    }

                    Pageable pageable = PageRequest.of(offset / limit, limit);

                    // Fetch items based on whether name filter is applied
                    if (name == null || name.isEmpty()) {
                        return employeeRepo.findAll(pageable).stream(); // Fetch all employees
                    } else {
                        return employeeRepo.findByName(name, pageable).stream(); // Filter by name
                    }
                },
                query -> {
                    // Return the total count based on whether name filter is applied
                    if (name == null || name.isEmpty()) {
                        return (int) employeeRepo.count(); // Total number of employees
                    } else {
                        return employeeRepo.findByNameWithoutPagination(name).size(); // Total number of filtered employees
                    }
                }
        );

        grid.setDataProvider(dataProvider);
    }
}
