package org.indp.vdbc.ui.settings;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileManager;

import java.util.List;

/**
 *
 */
public class SettingsManagerDialog extends Window implements ConnectionProfileDetailsPanel.ProfileListFacade {

    private ListSelect list;
    private ComponentContainer panel;

    public SettingsManagerDialog() {
    }

    @Override
    public void attach() {
        setModal(true);
        setResizable(false);
        setCaption("Settings");
        setWidth("600px");
        setHeight("420px");

        ComponentContainer workArea = createWorkArea();

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true);
        vl.addComponent(workArea);
        vl.setSizeFull();

        setContent(vl);

        refreshDetails();

        addAction(new CloseShortcut(this, ShortcutAction.KeyCode.ESCAPE));

        super.attach();

        this.focus();
    }

    private void refreshDetails() {
        panel.removeAllComponents();

        ConnectionProfile profile = (ConnectionProfile) list.getValue();
        if (profile == null) {
            return;
        }

        panel.addComponent(createDetails(profile));
    }

    private Component createDetails(ConnectionProfile profile) {
        ConnectionProfileManager<? extends ConnectionProfile> factory = ConnectionProfileManager.Lookup.find(profile.getClass());
        if (factory == null) {
            return new Label("Unknown profile type.");
        }

        ConnectionProfileDetailsPanel<? extends ConnectionProfile> propertiesPanel = factory.createPropertiesPanel(profile, this);
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(propertiesPanel);
        layout.setMargin(false, false, false, true);
        return layout;
    }

    private ComponentContainer createWorkArea() {
        panel = new VerticalLayout();
        panel.setSizeFull();

        ComponentContainer leftSide = createLeftSide();
        leftSide.setWidth("170px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponent(leftSide);
        layout.addComponent(panel);
        layout.setExpandRatio(panel, 1f);

        return layout;
    }

    private ComponentContainer createLeftSide() {
        List<ConnectionProfile> profiles = SettingsManager.get().getConfiguration().getProfiles();
        list = new ListSelect(null, new BeanItemContainer<ConnectionProfile>(ConnectionProfile.class, profiles));
        list.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        list.setItemCaptionPropertyId("name");
        list.setSizeFull();
        list.setNullSelectionAllowed(false);
        list.setImmediate(true);
        if (!profiles.isEmpty()) {
            list.select(profiles.get(0));
        }

        list.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refreshDetails();
            }
        });

        Button addButton = new Button("Add", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getApplication().getMainWindow().addWindow(new ProfileTypeSelectorDialog(new ProfileTypeSelectorDialog.SelectionListener() {
                    @Override
                    public void onFactorySelected(ConnectionProfileManager factory) {
                        createProfile(factory);
                    }
                }));
            }
        });

        Button saveProfilesButton = new Button("Save Profiles", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                SettingsManager.get().persistConfiguration();
                getApplication().getMainWindow().showNotification("Profiles saved.", Notification.TYPE_TRAY_NOTIFICATION);
            }
        });

        HorizontalLayout listButtons = new HorizontalLayout();
        listButtons.setSpacing(true);
        listButtons.addComponent(addButton);
        listButtons.addComponent(saveProfilesButton);

        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSizeFull();
        leftSide.setSpacing(true);
        leftSide.addComponent(list);
        leftSide.addComponent(listButtons);
        leftSide.setExpandRatio(list, 1f);

        return leftSide;
    }

    private void createProfile(ConnectionProfileManager factory) {
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
