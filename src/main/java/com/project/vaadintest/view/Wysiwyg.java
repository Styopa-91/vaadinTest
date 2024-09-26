package com.project.vaadintest.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route("wysiwyg")
@JsModule("./text-editor.js") // Подключаем наш компонент
public class Wysiwyg extends Div {
    public Wysiwyg() {
        // Создание Div для встроенного компонента
        Div editor = new Div();
        editor.getElement().setProperty("innerHTML", "<text-editor></text-editor>");

        // Добавление редактора в layout
        add(editor);

        Button getContentButton = new Button("Получить текст", event -> {
            editor.getElement().executeJs("return this.querySelector('text-editor').value")
                    .then(String.class, content -> {
                        Notification.show("Текущий текст: " + content);
                    });
        });

        add(getContentButton);
    }
}