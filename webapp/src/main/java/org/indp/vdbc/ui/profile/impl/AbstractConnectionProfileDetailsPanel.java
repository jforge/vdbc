package org.indp.vdbc.ui.profile.impl;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.config.ProfileField;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public abstract class AbstractConnectionProfileDetailsPanel<T extends ConnectionProfile> extends ConnectionProfileDetailsPanel<T> {

    private DetailsForm detailsForm;

    public AbstractConnectionProfileDetailsPanel(T profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    protected abstract DetailsForm createDetailsComponent();

    @Override
    protected synchronized Component getDetailsComponent() {
        if (detailsForm == null) {
            detailsForm = createDetailsComponent();
        }
        return detailsForm;
    }

    @Override
    protected void apply() {
        detailsForm.commit();
    }

    protected static class DetailsForm extends Form {

        private final Map<String, ProfileField> properties;

        public DetailsForm(ConnectionProfile profile, ProfileField... fields) {
            properties = new LinkedHashMap<String, ProfileField>();
            for (ProfileField field : fields) {
                properties.put(field.getId(), field);
            }

            setSizeFull();
            setWriteThrough(false);
            setInvalidCommitted(false);

            setFormFieldFactory(createFieldFactory());
            setItemDataSource(new BeanItem<ConnectionProfile>(profile));
            setVisibleItemProperties(properties.keySet().toArray());
        }

        protected FormFieldFactory createFieldFactory() {
            return new FieldFactory(properties);
        }
    }

    protected static class FieldFactory extends DefaultFieldFactory {
        private Map<String, ProfileField> properties;

        public FieldFactory(Map<String, ProfileField> properties) {
            this.properties = properties;
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            assert propertyId instanceof String;
            ProfileField profileField = properties.get(propertyId);
            return profileField == null ? null : profileField.getFactory().createField();
        }
    }
}
