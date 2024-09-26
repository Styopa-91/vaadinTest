class TextEditor extends HTMLElement {
    constructor() {
        super();
        const shadow = this.attachShadow({ mode: 'open' });

        // Create toolbar with basic formatting buttons
        const toolbar = document.createElement('div');
        toolbar.innerHTML = `
            <button type="button" data-command="bold">Bold</button>
            <button type="button" data-command="italic">Italic</button>
            <button type="button" data-command="underline">Underline</button>
            <button type="button" data-command="createLink">Insert Link</button>
        `;

        // Create the editable content area
        const editor = document.createElement('div');
        editor.style.width = '100%';
        editor.style.height = '300px';
        editor.style.border = '1px solid #ccc';
        editor.style.padding = '10px';
        editor.setAttribute('contenteditable', 'true');

        // Append the toolbar and the editor to the shadow DOM
        shadow.appendChild(toolbar);
        shadow.appendChild(editor);

        // Add event listener for toolbar buttons
        toolbar.addEventListener('click', (e) => {
            if (e.target.tagName === 'BUTTON') {
                const command = e.target.getAttribute('data-command');

                if (command === 'createLink') {
                    const url = prompt('Enter the link URL:');
                    document.execCommand(command, false, url);
                } else {
                    document.execCommand(command, false, null);
                }
            }
        });
    }

    // Get and set the inner HTML content of the editor
    get value() {
        return this.shadowRoot.querySelector('[contenteditable]').innerHTML;
    }

    set value(val) {
        this.shadowRoot.querySelector('[contenteditable]').innerHTML = val;
    }
}

customElements.define('text-editor', TextEditor);
