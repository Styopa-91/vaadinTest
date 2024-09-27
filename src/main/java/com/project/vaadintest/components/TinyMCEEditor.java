package com.project.vaadintest.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;

@Tag("div")
public class TinyMCEEditor extends Component implements HasValue<HasValue.ValueChangeEvent<String>, String> {

    private static final String TINYMCE_EDITOR_ID = "tinymce-editor";
    private boolean isTinyMCELoaded = false;

    public TinyMCEEditor() {
        getElement().getStyle().set("width", "100%");
        getElement().getStyle().set("height", "400px");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Page page = attachEvent.getUI().getPage();

        // Create a textarea for TinyMCE
        getElement().executeJs(
                "var textarea = document.createElement('textarea');" +
                        "textarea.id = '" + TINYMCE_EDITOR_ID + "';" +
                        "textarea.style.width = '100%';" +
                        "textarea.style.height = '100%';" +
                        "textarea.placeholder = 'Введите текст...';" +
                        "this.appendChild(textarea);"
        );

        loadTinyMCE(page); // Call loadTinyMCE() here
    }

    private void loadTinyMCE(Page page) {
        if (!isTinyMCELoaded) {
            page.executeJs(
                    "var script = document.createElement('script');" +
                            "script.src = 'https://cdn.tiny.cloud/1/arch1bp8ju8b0ojwyd0zsanzoeim1vagelpde9ufi3c9o6jm/tinymce/6/tinymce.min.js';" +
                            "script.referrerpolicy = 'origin';" +
                            "script.onload = function() {" +
                            "   window.tinymceLoaded = true;" +
                            "   initTinyMCE();" +
                            "};" +
                            "document.head.appendChild(script);" +
                            "function initTinyMCE() {" +
                            "   tinymce.init({" +
                            "       selector: '#" + TINYMCE_EDITOR_ID + "'," +
                            "       setup: function (editor) {" +
                            "           editor.on('change', function () {" +
                            "               editor.save();" +
                            "           });" +
                            "       }" +
                            "   });" +
                            "}"
            );
            isTinyMCELoaded = true; // Set the flag to true
        } else {
            page.executeJs("initTinyMCE();"); // Initialize if already loaded
        }
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            value = ""; // Handle null values appropriately
        }

        getElement().executeJs(
                "if (typeof tinymce !== 'undefined') {" +
                        "   const editor = tinymce.get($0); " +
                        "   if (editor) { editor.setContent($1); console.log('Content set'); } " +
                        "   else { console.log('Editor not found'); }" +
                        "} else {" +
                        "   var script = document.createElement('script');" +
                        "   script.src = 'https://cdn.tiny.cloud/1/arch1bp8ju8b0ojwyd0zsanzoeim1vagelpde9ufi3c9o6jm/tinymce/6/tinymce.min.js';" + // Use actual API key
                        "   script.onload = function() {" +
                        "       tinymce.init({" +
                        "           selector: '#" + TINYMCE_EDITOR_ID + "'," +
                        "           setup: function (editor) {" +
                        "               editor.on('change', function () {" +
                        "                   editor.save();" +
                        "               });" +
                        "               editor.on('init', function () {" +
                        "                   editor.setContent($1); console.log('Content set in editor: ' + $1);" +
                        "               });" +
                        "           }" +
                        "       });" +
                        "       const editor = tinymce.get($0); " +
                        "       if (editor) { editor.setContent($1); console.log($1); } " +
                        "       else { console.log('Editor not found after loading'); }" +
                        "   };" +
                        "   document.head.appendChild(script);" +
                        "   console.log('TinyMCE script loaded, will set content once loaded.');" +
                        "}",
                TINYMCE_EDITOR_ID, value);
    }

    public void getValueAsync(ValueCallback callback) {
        getElement().executeJs("return tinymce.get('" + TINYMCE_EDITOR_ID + "').getContent();")
                .then(result -> {
                    callback.onValueReceived(result.asString());
                });
    }

    public interface ValueCallback {
        void onValueReceived(String value);
    }

    @Override
    public String getValue() {
        // Use a callback to retrieve the content asynchronously
        final String[] result = {null}; // Array to hold the result
        getElement().executeJs("return tinymce.get('" + TINYMCE_EDITOR_ID + "').getContent();")
                .then(content -> {
                    result[0] = content.asString(); // Store the result
                });

        // Return null or a placeholder as the content will be retrieved asynchronously
        return result[0];
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<String>> valueChangeListener) {
        return null;
    }

    @Override
    public void setReadOnly(boolean b) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean b) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
