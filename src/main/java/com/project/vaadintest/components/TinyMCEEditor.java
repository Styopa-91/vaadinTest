package com.project.vaadintest.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextArea;

@Tag("div")
public class TinyMCEEditor extends Component {

    // A reference to the TinyMCE editor ID
    private static final String TINYMCE_EDITOR_ID = "tinymce-editor";

    public TinyMCEEditor() {
        // Set default styles for the editor container
        getElement().getStyle().set("width", "100%");
        getElement().getStyle().set("height", "400px");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Page page = attachEvent.getUI().getPage();

        // Inject the TinyMCE script and initialize the editor when the component is attached
        page.executeJs(
                "if (!window.tinymceLoaded) {" +
                        "   var script = document.createElement('script');" +
                        "   script.src = 'https://cdn.tiny.cloud/1/arch1bp8ju8b0ojwyd0zsanzoeim1vagelpde9ufi3c9o6jm/tinymce/6/tinymce.min.js';" +
                        "   script.referrerpolicy = 'origin';" +
                        "   script.onload = function() {" +
                        "       window.tinymceLoaded = true;" +
                        "       initTinyMCE();" +
                        "   };" +
                        "   document.head.appendChild(script);" +
                        "} else {" +
                        "   initTinyMCE();" +
                        "}" +
                        "function initTinyMCE() {" +
                        "   tinymce.init({" +
                        "       selector: '#" + TINYMCE_EDITOR_ID + "'," +
                        "       plugins: 'lists link image table'," + // Add your required plugins
                        "       toolbar: 'undo redo | styleselect | bold italic | alignleft aligncenter alignright | bullist numlist outdent indent | link image'," + // Define toolbar options
                        "       setup: function (editor) {" +
                        "           editor.on('change', function () {" +
                        "               editor.save();" + // Ensure changes are saved
                        "           });" +
                        "       }" +
                        "   });" +
                        "}"
        );

        // Create a textarea for TinyMCE
        getElement().executeJs(
                "var textarea = document.createElement('textarea');" +
                        "textarea.id = '" + TINYMCE_EDITOR_ID + "';" +
                        "textarea.style.width = '100%';" +
                        "textarea.style.height = '100%';" +
                        "textarea.placeholder = 'Введите текст...';" +
                        "this.appendChild(textarea);"
        );
    }

    public String getValue() {
        // Get the current value from the TinyMCE editor
        return getElement().executeJs("return tinymce.get('" + TINYMCE_EDITOR_ID + "').getContent();").toString();
    }

    public void setValue(String value) {
        // Set the value of the TinyMCE editor
        getElement().executeJs("tinymce.get('" + TINYMCE_EDITOR_ID + "').setContent($0);", value);
    }

    public void setWidth(String width) {
        getElement().getStyle().set("width", width);
    }

    public void setHeight(String height) {
        getElement().getStyle().set("height", height);
    }
}
