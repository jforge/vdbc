package org.indp.vdbc.profile.impl;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JndiConnectionProfile;
import org.indp.vdbc.profile.ConnectionProfileDetailsPanel; /**
 *
 */
public class JndiConnectionProfileDetailsPanel extends ConnectionProfileDetailsPanel<JndiConnectionProfile> {

    private DetailsForm detailsForm;

    public JndiConnectionProfileDetailsPanel(JndiConnectionProfile profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    @Override
    protected Component getDetailsComponent() {
        if (detailsForm == null) {
            detailsForm = new DetailsForm(getProfile());
        }
        return detailsForm;
    }

    @Override
    protected void apply() {
        detailsForm.commit();
    }

    private static class DetailsForm extends Form {

        private DetailsForm(ConnectionProfile profile) {
            setSizeFull();
            setCaption(profile.getName());
            setWriteThrough(false);
            setInvalidCommitted(false);

            setFormFieldFactory(new FieldFactory());
            setItemDataSource(new BeanItem<ConnectionProfile>(profile));
            setVisibleItemProperties(new String[]{"name", "jndiName"});
        }

    }

    private static class FieldFactory extends DefaultFieldFactory {
        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            TextField textField = new TextField(createCaptionByPropertyId(propertyId));
            textField.setWidth("100%");
            textField.setNullRepresentation("");
            textField.setRequired("jndiName".equals(propertyId));
            return textField;
        }
    }

}
