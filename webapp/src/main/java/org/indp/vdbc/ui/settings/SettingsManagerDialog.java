package org.indp.vdbc.ui.settings;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileSupportService;

import java.util.List;

/**
 *
 */
public class SettingsManagerDialog extends Window implements ConnectionProfileDetailsPanel.ProfileListFacade {

    private ListSelect list;
    private ComponentContainer detailsContainer;

    public SettingsManagerDialog() {
    }

    @Override
    public void attach() {
        super.attach();

        setModal(true);
        setResizable(false);
        setCaption("Settings");
        setWidth("600px");
        setHeight("420px");

        setContent(createContent());

        refreshDetails();

        addAction(new CloseShortcut(this, ShortcutAction.KeyCode.ESCAPE));

        this.focus();
    }

    private void refreshDetails() {
        detailsContainer.removeAllComponents();

        ConnectionProfile profile = (ConnectionProfile) list.getValue();
        if (profile == null) {
            return;
        }

        detailsContainer.addComponent(createDetails(profile));
    }

    private Component createDetails(ConnectionProfile profile) {
        ConnectionProfileSupportService<? extends ConnectionProfile> factory = ConnectionProfileSupportService.Lookup.find(profile.getClass());
        if (factory == null) {
            return new Label("Unknown profile type.");
        }

        return factory.createPropertiesPanel(profile, this);
    }

    private ComponentContainer createContent() {
        ComponentContainer leftSide = createLeftSide();
        leftSide.setWidth("170px");

        detailsContainer = new VerticalLayout();
        detailsContainer.setSizeFull();

        HorizontalLayout layout = new HorizontalLayout(leftSide, detailsContainer);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setSizeFull();
        layout.setExpandRatio(detailsContainer, 1f);

        return layout;
    }

    private ComponentContainer createLeftSide() {
        List<ConnectionProfile> profiles = SettingsManager.get().getConfiguration().getProfiles();
        list = new ListSelect(null, new BeanItemContainer<>(ConnectionProfile.class, profiles));
        list.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        list.setItemCaptionPropertyId("name");
        list.setSizeFull();
        list.setNullSelectionAllowed(false);
        list.setImmediate(true);
        if (!profiles.isEmpty()) {
            list.select(profiles.get(0));
        }

        list.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refreshDetails();
            }
        });

        Button addButton = new Button("Add", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getUI().addWindow(new ProfileTypeSelectorDialog(new ProfileTypeSelectorDialog.SelectionListener() {
                    @Override
                    public void onFactorySelected(ConnectionProfileSupportService factory) {
                        createProfile(factory);
                    }
                }));
            }
        });

        Button saveProfilesButton = new Button("Save Profiles", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                SettingsManager.get().persistConfiguration();
                Notification.show("Profiles saved.", Notification.Type.TRAY_NOTIFICATION);
            }
        });

        HorizontalLayout listButtons = new HorizontalLayout(addButton, saveProfilesButton);
        listButtons.setSpacing(true);

        VerticalLayout leftSide = new VerticalLayout(list, listButtons);
        leftSide.setSizeFull();
        leftSide.setSpacing(true);
        leftSide.setExpandRatio(list, 1f);

        return leftSide;
    }

    private void createProfile(ConnectionProfileSupportService factory) {
        ConnectionProfile profile = factory.createConnectionProfile();
        profile.setName("New Profile");
        SettingsManager.get().getConfiguration().addProfile(profile);
        list.getContainerDataSource().addItem(profile);
        list.select(profile);
    }

    @Override
    public ConnectionProfile getSelectedProfile() {
        return (ConnectionProfile) list.getValue();
    }

    @Override
    public void removeProfile(ConnectionProfile profile) {
        list.getContainerDataSource().removeItem(profile);
        list.select(null);
    }

    @Override
    public void profileUpdated(ConnectionProfile profile) {
        list.setItemCaption(profile, profile.getName());
    }
}
