package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.TextField;

public class SimpleProfileField extends AbstractTextProfileField {

    private TextField textField;

    public SimpleProfileField(String id) {
        super(id);
    }

    public SimpleProfileField(String id, String title, boolean required) {
        super(id, title, required);
    }

    @Override
    public AbstractTextField getFieldComponent() {
        if (textField == null) {
            textField = new TextField(getTitle());
            textField.setWidth("100%");
            textField.setNullRepresentation("");
            textField.setRequired(isRequired());
        }
        return textField;
    }
}
