package org.indp.vdbc.ui.profile.config;

import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;

/**
 *
 */
public abstract class AbstractProfileField {

    private final String id;
    private final String title;
    private final boolean required;
    private FormContext formContext;

    public AbstractProfileField(String id, String title, boolean required) {
        this.id = id;
        this.title = title;
        this.required = required;
    }

    public AbstractProfileField(String id) {
        this(id, DefaultFieldFactory.createCaptionByPropertyId(id), true);
    }

    public abstract Field getFieldComponent();

    public abstract void readValue();

    public abstract void writeValue();


    public FormContext getFormContext() {
        return formContext;
    }

    public void setFormContext(FormContext formContext) {
        this.formContext = formContext;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isRequired() {
        return required;
    }
}
