package org.indp.vdbc.ui.profile.config;

import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 *
 */
public class ProfileField {

    private String id;
    private FormFieldFactory factory;

    public ProfileField(String id, FormFieldFactory factory) {
        this.id = id;
        this.factory = factory;
    }

    public ProfileField(String id, String title, boolean required) {
        this(id, new DefaultFormFieldFactory(title, required));
    }

    public ProfileField(String id) {
        this(id, DefaultFieldFactory.createCaptionByPropertyId(id), true);
    }

    public String getId() {
        return id;
    }

    public FormFieldFactory getFactory() {
        return factory;
    }

    private static class DefaultFormFieldFactory extends AbstractFormFieldFactory {
        public DefaultFormFieldFactory(String title, boolean required) {
            super(title, required);
        }

        @Override
        public Field createField() {
            TextField textField = new TextField(getTitle());
            textField.setWidth("100%");
            textField.setNullRepresentation("");
            textField.setRequired(isRequired());
            return textField;
        }
    }
}
