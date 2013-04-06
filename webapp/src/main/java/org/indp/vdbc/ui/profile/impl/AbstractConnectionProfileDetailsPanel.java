package org.indp.vdbc.ui.profile.impl;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;
import org.indp.vdbc.ui.profile.config.FormContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class AbstractConnectionProfileDetailsPanel<T extends ConnectionProfile> extends ConnectionProfileDetailsPanel<T> {

    private Component detailsComponent;
    private Map<String, AbstractProfileField> fields;
    private final FormContext formContext = new FormContext() {
        @Override
        public AbstractProfileField getProfileField(String id) {
            return fields.containsKey(id) ? fields.get(id) : null;
        }

        @Override
        public ConnectionProfile getConnectionProfile() {
            return getProfile();
        }
    };

    public AbstractConnectionProfileDetailsPanel(T profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    protected abstract List<AbstractProfileField> getFields();

    @Override
    protected void apply() {
        for (AbstractProfileField profileField : fields.values()) {
            profileField.validate();
        }

        // write values to connection profile if we are still here
        for (AbstractProfileField profileField : fields.values()) {
            profileField.writeValue();
        }
    }

    @Override
    protected synchronized Component getDetailsComponent() {
        if (detailsComponent == null) {
            detailsComponent = createDetailsComponent();
        }
        return detailsComponent;
    }

    private Component createDetailsComponent() {
        List<AbstractProfileField> fields = getFields();
        this.fields = new LinkedHashMap<String, AbstractProfileField>(fields.size());

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("100%");

        for (AbstractProfileField field : fields) {
            field.setFormContext(formContext);
            Component vaadinField = field.getFieldComponent();
            this.fields.put(field.getId(), field);
            formLayout.addComponent(vaadinField);
        }

        for (AbstractProfileField field : this.fields.values()) {
            field.readValue();
        }

        return formLayout;
    }
}
