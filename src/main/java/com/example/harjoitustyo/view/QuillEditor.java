package com.example.harjoitustyo.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

@Tag("div")
@JavaScript("https://cdn.jsdelivr.net/npm/quill@1.3.7/dist/quill.min.js")
@StyleSheet("https://cdn.jsdelivr.net/npm/quill@1.3.7/dist/quill.snow.css")
public class QuillEditor extends Div {

    public QuillEditor() {
        addClassName("quill-host");
        setWidthFull();
        setHeight("280px");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        getElement().executeJs("""
                if (!this._quillReady) {
                  const editor = document.createElement('div');
                  editor.style.height = '210px';
                  editor.innerHTML = '<p>Write rich text notes here...</p>';
                  this.appendChild(editor);
                  this._quillReady = true;
                  this._quill = new Quill(editor, { theme: 'snow' });
                }
                """);
    }
}
