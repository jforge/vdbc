package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextField;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class SimpleProfileField extends AbstractProfileField {
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

    @Override
    public void readValue() {
        try {
            ConnectionProfile connectionProfile = getFormContext().getConnectionProfile();
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(connectionProfile);
            Object value = propertyDescriptor.getReadMethod().invoke(connectionProfile);
            String s = value == null ? "" : value.toString();
            getFieldComponent().setValue(s);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeValue() {
        try {
            ConnectionProfile connectionProfile = getFormContext().getConnectionProfile();
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(connectionProfile);
            propertyDescriptor.getWriteMethod().invoke(connectionProfile, getFieldComponent().getValue());
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private PropertyDescriptor getPropertyDescriptor(ConnectionProfile connectionProfile) throws IntrospectionException {
        return new PropertyDescriptor(getId(), connectionProfile.getClass());
    }
}
