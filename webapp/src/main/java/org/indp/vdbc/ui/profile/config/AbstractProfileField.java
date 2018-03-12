package org.indp.vdbc.ui.profile.config;

import com.vaadin.ui.Component;
import com.vaadin.v7.ui.DefaultFieldFactory;
import com.vaadin.v7.ui.Field;
import org.indp.vdbc.model.config.ConnectionProfile;

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

    public abstract Component getFieldComponent();

    public abstract void readValue();

    public abstract void writeValue();

    public void validate() {
        Component component = getFieldComponent();
        if (component instanceof Field) {
            ((Field) component).validate();
        }
    }

    protected ConnectionProfile getConnectionProfile() {
        return getFormContext().getConnectionProfile();
    }

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
