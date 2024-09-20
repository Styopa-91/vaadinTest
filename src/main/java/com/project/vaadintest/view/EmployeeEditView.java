package com.project.vaadintest.view;

import com.project.vaadintest.components.EmployeeEditor;
import com.project.vaadintest.domain.Employee;
import com.project.vaadintest.repo.EmployeeRepo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route("employee-edit")
public class EmployeeEditView extends VerticalLayout implements HasUrlParameter<String> {

    private final EmployeeRepo employeeRepo;
    private final EmployeeEditor editor;
    private final Button backButton = new Button("Back");

    @Autowired
    public EmployeeEditView(EmployeeRepo employeeRepo, EmployeeEditor editor) {
        this.employeeRepo = employeeRepo;
        this.editor = editor;

        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("employee-list")));
        add(backButton, editor);
        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String employeeId) {
        if ("new".equals(employeeId)) {
            editor.editEmployee(new Employee());
        } else if (employeeId != null) {
            try {
                Long id = Long.parseLong(employeeId);
                Employee employee = employeeRepo.findById(id).orElse(null);
                if (employee != null) {
                    editor.editEmployee(employee);
                } else {
                    Notification.show("Employee not found.");
                    getUI().ifPresent(ui -> ui.navigate("employee-list"));
                }
            } catch (NumberFormatException e) {
                Notification.show("Invalid employee ID.");
                getUI().ifPresent(ui -> ui.navigate("employee-list"));
            }
        } else {
            editor.setVisible(false); // Hide editor if no ID is provided
        }
    }
}
