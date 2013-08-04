package org.indp.vdbc.ui.settings;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.ConfirmDialog;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileSupportService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 */
public class SettingsManagerDialog extends Window implements ConnectionProfileDetailsPanel.ProfileEditorEvents {
    private Table profilesTable;
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

        ConnectionProfile profile = getSelectedProfile();
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
        Component leftSide = createLeftSide();
        leftSide.setWidth("170px");

        detailsContainer = new VerticalLayout();
        detailsContainer.setSizeFull();

        HorizontalLayout mainContent = new HorizontalLayout(leftSide, detailsContainer);
        mainContent.setSpacing(true);
        mainContent.setSizeFull();
        mainContent.setExpandRatio(detailsContainer, 1f);

        VerticalLayout root = new VerticalLayout(createToolbar(), mainContent);
        root.setSizeFull();
        root.setExpandRatio(mainContent, 1);
        root.setMargin(true);
        root.setSpacing(true);
        return root;
    }

    private Component createLeftSide() {
        List<ConnectionProfile> profiles = SettingsManager.get().getConfiguration().getProfiles();
        profilesTable = new Table(null, new BeanItemContainer<>(ConnectionProfile.class, profiles));
        profilesTable.setVisibleColumns("name");
        profilesTable.setSizeFull();
        profilesTable.setNullSelectionAllowed(false);
        profilesTable.setImmediate(true);
        profilesTable.setSelectable(true);
        profilesTable.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        if (!profiles.isEmpty()) {
            profilesTable.select(profiles.get(0));
        }

        profilesTable.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                refreshDetails();
            }
        });

        return profilesTable;
    }

    private Component createToolbar() {
        Button addButton = new Button("Create Profile", new Button.ClickListener() {
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

        Button removeButton = new Button("Remove Profile", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConnectionProfile value = getSelectedProfile();
                if (value != null) {
                    removeProfile(value);
                } else {
                    Notification.show("Select some profile to remove it");
                }
            }
        });


        HorizontalLayout toolbarLayout = new HorizontalLayout(addButton, removeButton, saveProfilesButton);
        toolbarLayout.setSpacing(true);
        return toolbarLayout;
    }

    private void createProfile(ConnectionProfileSupportService factory) {
        ConnectionProfile profile = factory.createConnectionProfile();
        profile.setName("New Profile");
        SettingsManager.get().getConfiguration().addProfile(profile);
        profilesTable.getContainerDataSource().addItem(profile);
        profilesTable.select(profile);
    }

    private ConnectionProfile getSelectedProfile() {
        return (ConnectionProfile) profilesTable.getValue();
    }

    private void removeProfile(final ConnectionProfile profile) {
        ConfirmDialog.confirmYesNo("Remove profile \"" + profile.getName() + "\"?", "Remove", "No", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsManager.get().getConfiguration().removeProfile(profile);
                profilesTable.getContainerDataSource().removeItem(profile);
                profilesTable.select(null);
            }
        });
    }

    @Override
    public void profileUpdated(ConnectionProfile profile) {
        profilesTable.setItemCaption(profile, profile.getName());
    }
}
