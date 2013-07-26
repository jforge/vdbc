package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.ui.AbstractTextField;

/**
 *
 */
public class PasswordField extends SimpleProfileField {

    private com.vaadin.ui.PasswordField field;

    public PasswordField(String id, String title, boolean required) {
        super(id, title, required);
    }

    @Override
    public AbstractTextField getFieldComponent() {
        if (field == null) {
            field = new com.vaadin.ui.PasswordField(getTitle());
            field.setWidth("100%");
            field.setNullRepresentation("");
            field.setRequired(isRequired());
        }
        return field;
    }
}
