package org.indp.vdbc.profile.impl;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.profile.ConnectionProfileDetailsPanel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        private final String[] optionalProperties;

        public DetailsForm(ConnectionProfile profile, String[] visibleProperties, String[] optionalProperties) {
            this.optionalProperties = optionalProperties;
            setSizeFull();
            setCaption(profile.getName());
            setWriteThrough(false);
            setInvalidCommitted(false);

            setFormFieldFactory(createFieldFactory());
            setItemDataSource(new BeanItem<ConnectionProfile>(profile));
            setVisibleItemProperties(visibleProperties);
        }

        protected FormFieldFactory createFieldFactory() {
            return new FieldFactory(optionalProperties);
        }
    }

    protected static class FieldFactory extends DefaultFieldFactory {
        private final List<String> optionalProperties;

        public FieldFactory() {
            optionalProperties = Collections.emptyList();
        }

        public FieldFactory(String[] optionalProperties) {
            this.optionalProperties = Arrays.asList(optionalProperties);
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            TextField textField = new TextField(createCaptionByPropertyId(propertyId));
            textField.setWidth("100%");
            textField.setNullRepresentation("");
            textField.setRequired(isRequired(propertyId.toString()));
            return textField;
        }

        private boolean isRequired(String s) {
            return !optionalProperties.contains(s);
        }
    }
}
