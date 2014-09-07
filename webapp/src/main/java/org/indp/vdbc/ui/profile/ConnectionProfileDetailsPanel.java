package org.indp.vdbc.ui.profile;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;
import org.indp.vdbc.ui.profile.config.FormContext;
import org.indp.vdbc.ui.profile.impl.fields.ColorField;
import org.indp.vdbc.ui.profile.impl.fields.DialectField;
import org.indp.vdbc.ui.profile.impl.fields.SimpleProfileField;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ConnectionProfileDetailsPanel<T extends ConnectionProfile> extends CustomComponent {

    private Map<String, AbstractProfileField> fields;
    private T profile;

    private final FormContext formContext = new FormContext() {
        @Override
        public AbstractProfileField getProfileField(String id) {
            return fields.containsKey(id) ? fields.get(id) : null;
        }

        @Override
        public ConnectionProfile getConnectionProfile() {
            return profile;
        }
    };

    protected ConnectionProfileDetailsPanel(T profile) {
        this.profile = profile;
    }

    @Override
    public void attach() {
        super.attach();
        setCompositionRoot(createContent());
    }

    protected List<AbstractProfileField> getFields() {
        return Arrays.asList(
                new SimpleProfileField("name"),
                new DialectField("dialect", "Dialect", true),
                new ColorField("color", "Color", false));
    }

    public void apply() {
        for (AbstractProfileField profileField : fields.values()) {
            profileField.validate();
        }

        // write values to connection profile if we are still here
        for (AbstractProfileField profileField : fields.values()) {
            profileField.writeValue();
        }
    }

    public T createTemporaryProfile() {
        // todo rewrite profile fields, form context and other stuff to avoid such hackish things
        T originalProfile = profile;
        ConnectionProfileSupportService<? extends ConnectionProfile> connectionProfileSupportService = ConnectionProfileSupportService.Lookup.find(profile.getClass());
        try {
            T tempProfile = (T) connectionProfileSupportService.createConnectionProfile();
            profile = tempProfile;
            apply();
            return tempProfile;
        } finally {
            profile = originalProfile;
        }
    }

    private Component createContent() {
        List<AbstractProfileField> formFields = getFields();
        fields = new LinkedHashMap<>(formFields.size());

        FormLayout formLayout = new FormLayout();
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Focusable focusedField = null;

        for (AbstractProfileField field : formFields) {
            field.setFormContext(formContext);
            Component vaadinField = field.getFieldComponent();
            fields.put(field.getId(), field);
            formLayout.addComponent(vaadinField);
            if (vaadinField instanceof Focusable && focusedField == null) {
                focusedField = (Focusable) vaadinField;
            }
        }

        for (AbstractProfileField field : fields.values()) {
            field.readValue();
        }

        if (focusedField != null) {
            focusedField.focus();
        }

        return formLayout;
    }
}
