package org.indp.vdbc.ui.settings;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
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
        setModal(true);
        setResizable(false);
        setCaption("Settings");
        setWidth("600px");
        setHeight("420px");

        GridLayout layout = createMainLayout();
        setContent(layout);

        Component bottom = createBottomBar();
        layout.addComponent(bottom, 0, 1);
        layout.setComponentAlignment(bottom, Alignment.MIDDLE_RIGHT);
        layout.setRowExpandRatio(0, 1);

        refreshDetails();
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

    private GridLayout createMainLayout() {
        GridLayout layout = new GridLayout(1, 2);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(createWorkArea(), 0, 0);
        layout.setSizeFull();
        return layout;
    }

    private Component createWorkArea() {
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        split.setSizeFull();
        split.setSplitPosition(30);

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

        panel = new VerticalLayout();
        panel.setSizeFull();

        split.addComponent(list);
        split.addComponent(panel);

        return split;
    }

    private Component createBottomBar() {
        HorizontalLayout bottom = new HorizontalLayout();
        bottom.setWidth("100%");
        bottom.setSpacing(true);

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
            }
        });

        Button closeButton = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

        bottom.addComponent(addButton);
        bottom.addComponent(saveProfilesButton);
        bottom.addComponent(closeButton);

        bottom.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);
        bottom.setExpandRatio(closeButton, 1);

        return bottom;
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
}
