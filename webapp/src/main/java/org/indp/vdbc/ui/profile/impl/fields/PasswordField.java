package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.v7.ui.AbstractTextField;

public class PasswordField extends AbstractTextProfileField {

    private com.vaadin.v7.ui.PasswordField field;

    public PasswordField(String id, String title, boolean required) {
        super(id, title, required);
    }

    @Override
    public AbstractTextField getFieldComponent() {
        if (field == null) {
            field = new com.vaadin.v7.ui.PasswordField(getTitle());
            field.setWidth("100%");
            field.setNullRepresentation("");
            field.setRequired(isRequired());
        }
        return field;
    }
}
