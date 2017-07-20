package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.TextArea;

public class TextAreaProfileField extends AbstractTextProfileField {

    private final int rows;
    private TextArea textField;

    public TextAreaProfileField(String id) {
        super(id);
        rows = 4;
    }

    public TextAreaProfileField(String id, String title, int rows, boolean required) {
        super(id, title, required);
        this.rows = rows;
    }

    @Override
    public AbstractTextField getFieldComponent() {
        if (textField == null) {
            textField = new TextArea(getTitle());
            textField.setWidth("100%");
            textField.setNullRepresentation("");
            textField.setRequired(isRequired());
            textField.setRows(rows);
        }
        return textField;
    }
}
