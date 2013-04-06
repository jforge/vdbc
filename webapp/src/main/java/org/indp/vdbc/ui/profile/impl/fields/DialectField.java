package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import org.indp.vdbc.db.Dialect;
import org.indp.vdbc.db.DialectSupport;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class DialectField extends AbstractProfileField {

    private ComboBox comboBox;

    public DialectField(String id, String title, boolean required) {
        super(id, title, required);
    }

    @Override
    public Field getFieldComponent() {
        if (comboBox == null) {
            Collection<String> dialectCodes = DialectSupport.getDialectIds();
            comboBox = new ComboBox(getTitle(), dialectCodes);
            comboBox.setWidth("100%");
            comboBox.setImmediate(true);
            comboBox.setNullSelectionAllowed(false);
            comboBox.setInvalidAllowed(false);
            comboBox.setRequired(isRequired());
            for (String code : dialectCodes) {
                comboBox.setItemCaption(code, DialectSupport.getDialect(code).getName());
            }
            comboBox.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    Object value = valueChangeEvent.getProperty().getValue();
                    updateDependencies(value.toString());
                }
            });
        }
        return comboBox;
    }

    @Override
    public void readValue() {
        ConnectionProfile connectionProfile = getFormContext().getConnectionProfile();
        String dialectName = connectionProfile.getDialect();
        comboBox.setValue(dialectName);

        updateDependencies(dialectName);
    }

    private void updateDependencies(String dialectName) {
        ComboBox driverField = (ComboBox) getFormContext().getField("driver");
        ComboBox urlField = (ComboBox) getFormContext().getField("url");
        if (driverField != null && urlField != null) {
            Object oldDriver = driverField.getValue();
            Object oldUrl = urlField.getValue();

            driverField.removeAllItems();
            urlField.removeAllItems();

            Dialect dialect = DialectSupport.getDialect(dialectName);
            if (dialect != null) {
                List<String> drivers = dialect.getDrivers();
                if (drivers != null) {
                    for (String driver : drivers) {
                        driverField.addItem(driver);
                    }
                }
                List<String> exampleUrls = dialect.getExampleUrls();
                if (exampleUrls != null) {
                    for (String url : exampleUrls) {
                        urlField.addItem(url);
                    }
                }
            }

            if (oldDriver != null && !driverField.containsId(oldDriver)) {
                driverField.addItem(oldDriver);
            }
            if (oldUrl != null && !urlField.containsId(oldUrl)) {
                urlField.addItem(oldUrl);
            }
            driverField.setValue(oldDriver);
            urlField.setValue(oldUrl);
        }
    }

    @Override
    public void writeValue() {
        Object value = comboBox.getValue();
        getFormContext().getConnectionProfile().setDialect(value == null ? null : value.toString());
    }
}
