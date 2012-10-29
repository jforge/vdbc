package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.ui.Field;
import com.vaadin.ui.PasswordField;
import org.indp.vdbc.ui.profile.config.AbstractFormFieldFactory;

/**
 *
 */
public class PasswordFieldFactory extends AbstractFormFieldFactory {

    public PasswordFieldFactory(String title, boolean required) {
        super(title, required);
    }

    @Override
    public Field createField() {
        PasswordField field = new PasswordField(getTitle());
        field.setWidth("100%");
        field.setNullRepresentation("");
        field.setRequired(isRequired());
        return field;
    }
}
