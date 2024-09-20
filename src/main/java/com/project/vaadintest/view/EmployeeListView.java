package com.project.vaadintest.view;

import com.project.vaadintest.domain.Employee;
import com.project.vaadintest.repo.EmployeeRepo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Route("employee-list")
public class EmployeeListView extends VerticalLayout {

    private final EmployeeRepo employeeRepo;
    private final Grid<Employee> grid = new Grid<>(Employee.class);

    @Autowired
    public EmployeeListView(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;

        Button backButton = new Button("Back to Main View", event ->
                getUI().ifPresent(ui -> ui.navigate(MainView.class)));
        add(backButton);

        Button addNewBtn = new Button("Add new");

        addNewBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("employee-edit/new")));

        grid.setPageSize(5);
        grid.setWidth("100%");

        Grid.Column<Employee> idColumn = grid.addColumn(new ComponentRenderer<>(employee -> {
            Button idButton = new Button(String.valueOf(employee.getId()));
            idButton.addClickListener(click -> {
                getUI().ifPresent(ui -> ui.navigate("employee-edit/" + employee.getId()));
            });
            return idButton; // Return the button to be displayed in the grid
        })).setHeader("ID").setFlexGrow(0);

//        grid.addColumns("lastName", "firstName", "patronymic", "description", "birthDate");
//        Grid.Column<Employee> idColumn = grid.getColumnByKey("id");
//        Grid.Column<Employee> idNativeColumn = grid.getColumnByKey("id");
        Grid.Column<Employee> lastNameColumn = grid.getColumnByKey("lastName");
        Grid.Column<Employee> firstNameColumn = grid.getColumnByKey("firstName");
        Grid.Column<Employee> patronymicColumn = grid.getColumnByKey("patronymic");
        Grid.Column<Employee> descriptionColumn = grid.getColumnByKey("description");
        Grid.Column<Employee> birthDateColumn = grid.getColumnByKey("birthDate");
        grid.removeColumnByKey("id");
        List<Grid.Column<Employee>> orderedColumns = new ArrayList<>();
        orderedColumns.add(idColumn); // Add the ID column first
//        orderedColumns.addAll(grid.getColumns().stream()
//                .filter(column -> !column.equals(idColumn))// Exclude the ID column to avoid duplication
//                .toList()); // Add remaining columns

//        orderedColumns.add(idNativeColumn); // Add the Last Name column
        orderedColumns.add(lastNameColumn); // Add the Last Name column
        orderedColumns.add(firstNameColumn);// Add the First Name column
        orderedColumns.add(patronymicColumn); // Add the Patronymic column
        orderedColumns.add(descriptionColumn); // Add the Patronymic column
        orderedColumns.add(birthDateColumn);
        // Set the column order
        grid.setColumnOrder(orderedColumns);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        add(addNewBtn, grid);
        showEmployees();
    }

    private void showEmployees() {
        CallbackDataProvider<Employee, Void> dataProvider = new CallbackDataProvider<>(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    if (limit == 0) {
                        return Stream.empty();
                    }

                    Pageable pageable = PageRequest.of(offset / limit, limit);
                    return employeeRepo.findAll(pageable).stream(); // Fetch all employees
                },
                query -> (int) employeeRepo.count() // Total number of employees
        );

        grid.setDataProvider(dataProvider);
    }
}
