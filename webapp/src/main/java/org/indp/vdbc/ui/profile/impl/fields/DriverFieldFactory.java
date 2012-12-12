package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import org.indp.vdbc.ui.profile.config.AbstractFormFieldFactory;

/**
 *
 */
public class DriverFieldFactory extends AbstractFormFieldFactory {

    public DriverFieldFactory(String title, boolean required) {
        super(title, required);
    }

    @Override
    public Field createField() {
        ComboBox box = new ComboBox(getTitle());
        box.setImmediate(true);
        box.setRequired(isRequired());
        box.setInputPrompt("Type or select JDBC driver class name");
        box.setNullSelectionAllowed(false);
        box.setInvalidAllowed(true);
        box.setNewItemsAllowed(true);
        return box;

    }
}
