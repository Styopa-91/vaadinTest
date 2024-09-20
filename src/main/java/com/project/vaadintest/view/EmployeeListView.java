package com.project.vaadintest.view;

import com.project.vaadintest.domain.Employee;
import com.project.vaadintest.repo.EmployeeRepo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        grid.setColumns("id", "lastName", "firstName", "patronymic", "description", "birthDate");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.addItemClickListener(event -> {
            Employee selectedEmployee = event.getItem();
            getUI().ifPresent(ui -> ui.navigate("employee-edit/" + selectedEmployee.getId()));
        });

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
