package com.project.vaadintest.view;

import com.project.vaadintest.components.EmployeeEditor;
import com.project.vaadintest.domain.Employee;
import com.project.vaadintest.repo.EmployeeRepo;
import com.vaadin.flow.component.button.Button;
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

@Route
public class MainView extends VerticalLayout {

    private final EmployeeRepo employeeRepo;

    private final Grid<Employee> grid = new Grid<>(Employee.class);

    private final TextField filter = new TextField("", "Type to filter");

    @Autowired
    public MainView(EmployeeRepo employeeRepo, EmployeeEditor editor) {
        this.employeeRepo = employeeRepo;
        Button addNewBtn = new Button("Add new");
        HorizontalLayout toolbar = new HorizontalLayout(filter, addNewBtn);
        HorizontalLayout main = new HorizontalLayout(grid, editor);
        main.setSizeFull();
        grid.setPageSize(5);  // This sets how many rows to fetch at once.
        grid.setWidth("100%");  // Адаптивная ширина

        grid.setColumns("id", "lastName", "firstName", "patronymic", "birthDate");
        editor.setWidth("auto");  // Адаптивная ширина для редактора
        main.setFlexGrow(1, grid);
        add(toolbar, main);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> showEmployee(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editEmployee(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editEmployee(new Employee()));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            showEmployee(filter.getValue());
        });
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
