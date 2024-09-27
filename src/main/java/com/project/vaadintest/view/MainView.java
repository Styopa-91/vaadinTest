package com.project.vaadintest.view;

import com.project.vaadintest.components.EmployeeEditor;
import com.project.vaadintest.domain.Employee;
import com.project.vaadintest.repo.EmployeeRepo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;


@Route
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private final EmployeeRepo employeeRepo;

    private final Grid<Employee> grid = new Grid<>(Employee.class);

    private final TextField filter = new TextField("", "Type to filter");

    private final HorizontalLayout paginationLayout = new HorizontalLayout();
    private int currentPage = 0; // Current page number
    private final int itemsPerPage = 5; // Number of items per page
    private final int visiblePages = 5;
    private ListDataProvider<Employee> dataProvider;

    @Autowired
    public MainView(EmployeeRepo employeeRepo, EmployeeEditor editor) {
        this.employeeRepo = employeeRepo;
        Button toViewModal = new Button("Go to ViewModal", event ->
                getUI().ifPresent(ui -> ui.navigate(ModalView.class)));
        Button toList = new Button("Go to Employee List", event ->
                getUI().ifPresent(ui -> ui.navigate(EmployeeListView.class)));
        Button toDepartments = new Button("Go to Departments List", event ->
                getUI().ifPresent(ui -> ui.navigate(ModalViewDepartment.class)));

        Button addNewBtn = new Button("Add new");
        HorizontalLayout toolbar = new HorizontalLayout(toViewModal, toList, toDepartments, filter, addNewBtn);
        HorizontalLayout main = new HorizontalLayout(grid, editor);
        main.setSizeFull();
        grid.setPageSize(itemsPerPage);  // This sets how many rows to fetch at once.
        grid.setWidth("100%");  // Адаптивная ширина

        grid.setColumns("id", "lastName", "firstName", "patronymic", "description", "birthDate");
        editor.setWidth("auto");  // Адаптивная ширина для редактора
        main.setFlexGrow(1, grid);
        add(toolbar, main, paginationLayout);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> updateView());

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editEmployee(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editEmployee(new Employee()));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            updateView();
        });

        updateView();
//        showEmployee("");
    }

    private void updateView() {
        loadData(); // Загружаем данные
        updatePaginationLayout(); // Обновляем элементы пагинации
    }

    private void loadData() {
        Pageable pageable = PageRequest.of(currentPage, itemsPerPage);
        List<Employee> employees;

        // Fetch data based on filter value
        if (filter.getValue() == null || filter.getValue().isEmpty()) {
            employees = employeeRepo.findAll(pageable).getContent();
        } else {
            employees = employeeRepo.findByName(filter.getValue(), pageable).getContent();
        }

        // Create a new modifiable list from the fetched employees
        List<Employee> modifiableEmployees = new ArrayList<>(employees);

        if (dataProvider == null) {
            // Create a new ListDataProvider and set it to the grid
            dataProvider = new ListDataProvider<>(modifiableEmployees);
            grid.setDataProvider(dataProvider);
        } else {
            // If dataProvider is already set, simply replace its items
            dataProvider.getItems().clear();
            dataProvider.getItems().addAll(modifiableEmployees);

            // Refresh the data provider to update the view
            dataProvider.refreshAll();
        }
    }



    private void updatePaginationLayout() {
        paginationLayout.removeAll();
        int totalItems = (int) employeeRepo.count();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        int startPage = Math.max(0, currentPage - visiblePages / 2);
        int endPage = Math.min(totalPages, startPage + visiblePages);

        if (currentPage > 0) {
            paginationLayout.add(createPageButton("<<", 0));
            paginationLayout.add(createPageButton("<", currentPage - 1));
        }

        for (int i = startPage; i < endPage; i++) {
            Button pageButton = createPageButton(String.valueOf(i + 1), i);
            if (i == currentPage) {
                pageButton.setEnabled(false);
            }
            paginationLayout.add(pageButton);
        }

        if (currentPage < totalPages - 1) {
            paginationLayout.add(createPageButton(">", currentPage + 1));
            paginationLayout.add(createPageButton(">>", totalPages - 1));
        }
    }

    private Button createPageButton(String text, int pageNumber) {
        Button button = new Button(text);
        button.addClickListener(event -> navigateToPage(pageNumber));
        return button;
    }

    private void navigateToPage(int pageNumber) {
        currentPage = pageNumber;
        updateView();

        // Обновляем URL с текущей страницей
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(currentPage + 1));
        getUI().ifPresent(ui -> ui.navigate("", QueryParameters.simple(params)));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Получаем текущую страницу из параметра URL
        Optional<String> page = event.getLocation().getQueryParameters().getParameters()
                .getOrDefault("page", List.of("1")).stream().findFirst();
        currentPage = page.map(p -> Integer.parseInt(p) - 1).orElse(0); // Переводим из 1-базированного в 0-базированный индекс
        updateView();
    }
}