package org.indp.vdbc.ui.settings;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.ui.*;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 */
public class DetailsPanel extends CustomComponent {

    private final InfoForm form;
    private ListSelect profileListSelect;

    public DetailsPanel(ConnectionProfile profile, ListSelect list) {
        profileListSelect = list;
        form = new InfoForm(profile);
        setCompositionRoot(form);
    }

    public void apply() {
        form.commit();
    }

    private class InfoForm extends Form {

        private InfoForm(ConnectionProfile profile) {
            setSizeFull();
            setCaption(profile.getName());
            setWriteThrough(false);
            setInvalidCommitted(false);

            setFormFieldFactory(new FieldFactory());
            setItemDataSource(new BeanItem<ConnectionProfile>(profile));
            setVisibleItemProperties(new String[]{"name", "driver", "url", "user", "password", "validationQuery"});

            HorizontalLayout footer = new HorizontalLayout();
            footer.setSpacing(true);
            footer.addComponent(new Button("Apply", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        apply();
                    } catch (Exception e) {
                        if (e instanceof ErrorMessage) {
                            getWindow().showNotification("Check required values", Window.Notification.TYPE_WARNING_MESSAGE);
                        }
                    }
                }
            }));
            footer.addComponent(new Button("Remove", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    ConnectionProfile value = (ConnectionProfile) profileListSelect.getValue();
                    if (value != null) {
                        SettingsManager.get().getConfiguration().removeProfile(value);
                        profileListSelect.getContainerDataSource().removeItem(value);
                        profileListSelect.select(null);
                    } else {
                        getWindow().showNotification("Select some profile to remove it");
                    }
                }
            }));

            setFooter(footer);
        }

    }

    private static class FieldFactory extends DefaultFieldFactory {
        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            TextField textField = new TextField(createCaptionByPropertyId(propertyId));
            textField.setWidth("100%");
            textField.setNullRepresentation("");
            if (!("password".equals(propertyId) || "validationQuery".equals(propertyId))) {
                textField.setRequired(true);
            }
            return textField;
        }
    }
}
