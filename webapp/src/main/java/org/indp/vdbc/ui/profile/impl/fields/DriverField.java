package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;

/**
 *
 */
public class DriverField extends AbstractProfileField {

    private ComboBox comboBox;

    public DriverField(String id, String title, boolean required) {
        super(id, title, required);
    }

    @Override
    public Field getFieldComponent() {
        if (comboBox == null) {
            comboBox = new ComboBox(getTitle());
            comboBox.setWidth("100%");
            comboBox.setImmediate(true);
            comboBox.setRequired(isRequired());
            comboBox.setNullSelectionAllowed(false);
            comboBox.setInvalidAllowed(true);
            comboBox.setNewItemsAllowed(true);
        }
        return comboBox;
    }

    @Override
    public void readValue() {
        ConnectionProfile connectionProfile = getFormContext().getConnectionProfile();
        assert connectionProfile instanceof JdbcConnectionProfile;

        String driver = ((JdbcConnectionProfile) connectionProfile).getDriver();
        if (driver != null && !comboBox.containsId(driver)) {
            comboBox.addItem(driver);
        }
        comboBox.setValue(driver);
    }

    @Override
    public void writeValue() {
        ConnectionProfile connectionProfile = getFormContext().getConnectionProfile();
        assert connectionProfile instanceof JdbcConnectionProfile;
        Object value = comboBox.getValue();
        ((JdbcConnectionProfile) connectionProfile).setDriver(value == null ? null : value.toString());
    }
}
