package com.project.vaadintest.view;

import com.project.vaadintest.components.TinyMCEEditor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("tiny")
public class TinyView extends VerticalLayout {

    public TinyView() {
        TinyMCEEditor editor = new TinyMCEEditor();
//        editor.setWidth("100%");
//        editor.setHeight("400px");

        add(editor);

        // Add a button to get the content from TinyMCE
        Button getContentButton = new Button("Get Content", event -> {
            String content = editor.getValue();
            System.out.println("Content: " + content);
        });

        add(getContentButton);
    }
}
