package org.indp.vdbc.ui;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanelFactory;
import org.indp.vdbc.ui.profile.ConnectionProfileSupportService;
import org.indp.vdbc.ui.settings.ProfileSettingsDialog;
import org.indp.vdbc.ui.settings.ProfileTypeSelectorDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ConnectionSelectorComponent extends VerticalLayout {

    private final DatabaseSessionManager databaseSessionManager;
    private Panel rootPanel;
    private Table profileSelector;

    public ConnectionSelectorComponent(DatabaseSessionManager databaseSessionManager) {
        this.databaseSessionManager = databaseSessionManager;
    }

    @Override
    public void attach() {
        super.attach();

        rootPanel = new Panel("Connect to...");
        rootPanel.addStyleName(ValoTheme.PANEL_WELL);
        rootPanel.setWidth("500px");
        rootPanel.setHeight("300px");
        recreateRootLayout();

        setSizeFull();
        addComponent(rootPanel);
        setComponentAlignment(rootPanel, Alignment.MIDDLE_CENTER);
    }

    private void recreateRootLayout() {
        List<ConnectionProfile> profiles = SettingsManager.get().getConfiguration().getProfiles();
        Component component = profiles.isEmpty()
                ? createProfileEditorInvite()
                : createConnectionSelectorLayout();
        rootPanel.setContent(component);
    }

    private Component createConnectionSelectorLayout() {
        final ProfileInfoPanelHolder infoPanelHolder = new ProfileInfoPanelHolder();
        profileSelector = createProfileSelector(infoPanelHolder);
        HorizontalLayout content = new HorizontalLayout(profileSelector, infoPanelHolder);
        content.setSizeFull();
        content.setExpandRatio(infoPanelHolder, 1);

        Component buttons = createConnectionButtons(infoPanelHolder);
        Component profileManagerToolbar = createProfileManagerToolbar(profileSelector);

        HorizontalLayout footer = new HorizontalLayout(profileManagerToolbar, buttons);
        footer.setWidth("100%");
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        VerticalLayout layout = new VerticalLayout(content, footer);
        layout.setSizeFull();
        layout.setExpandRatio(content, 1);

        return layout;
    }

    private Component createProfileManagerToolbar(final Table profileSelector) {
        if (!SettingsManager.get().isSettingsEditorEnabled()) {
            return new Label();
        }

        MenuBar toolbar = new MenuBar();
        toolbar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);

        createToolbarItem(toolbar, "Create Profile", FontAwesome.PLUS, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                addProfile();
            }
        });

        createToolbarItem(toolbar, "Edit Profile", FontAwesome.PENCIL, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                ConnectionProfile value = (ConnectionProfile) profileSelector.getValue();
                if (value != null) {
                    editProfile(value);
                }
            }
        });

        createToolbarItem(toolbar, "Remove Profile", FontAwesome.TRASH_O, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                ConnectionProfile value = (ConnectionProfile) profileSelector.getValue();
                if (value != null) {
                    removeProfile(value);
                }
            }
        });

        return toolbar;
    }

    private void createToolbarItem(MenuBar toolbar, String hint, FontAwesome icon, MenuBar.Command command) {
        MenuBar.MenuItem item = toolbar.addItem("", command);
        item.setIcon(icon);
        item.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        item.setDescription(hint);
    }

    private Component createConnectionButtons(final ProfileInfoPanelHolder infoPanelHolder) {
        Button connectButton = new Button("Connect", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConnectionProfileLoginPanelFactory panel = infoPanelHolder.getPanelFactory();
                ConnectionProfile profile = panel.createConnectionProfile();
                try {
                    databaseSessionManager.createSession(profile);
                } catch (Exception ex) {
                    Notification.show("Failed to connect\n", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        connectButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        HorizontalLayout buttons = new HorizontalLayout(connectButton);
        buttons.setSpacing(true);
        return buttons;
    }

    private Table createProfileSelector(final ProfileInfoPanelHolder infoPanelHolder) {
        ProfileListTable table = new ProfileListTable(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                ConnectionProfile cp = (ConnectionProfile) event.getProperty().getValue();
                if (cp == null) {
                    return;
                }
                ConnectionProfileSupportService<? extends ConnectionProfile> connectionProfileSupportService = ConnectionProfileSupportService.Lookup.find(cp.getClass());
                ConnectionProfileLoginPanelFactory<? extends ConnectionProfile> loginPanel = connectionProfileSupportService.createLoginPanel(cp);
                infoPanelHolder.setPanelFactory(loginPanel);
                infoPanelHolder.focus();
            }
        });

        table.setWidth("200px");

        return table;
    }

    private Component createProfileEditorInvite() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        if (!SettingsManager.get().isSettingsEditorEnabled()) {
            layout.addComponent(new Label("No connection profiles defined and you are not allowed to create one."));
        } else {
            Button createProfileLink = new Button("Create profile", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    addProfile();
                }
            });
            createProfileLink.setStyleName(ValoTheme.BUTTON_LINK);

            Label line1 = new Label("No connection profiles defined.");
            line1.setSizeUndefined();

            VerticalLayout inner = new VerticalLayout();
            inner.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
            inner.addComponents(line1, createProfileLink);

            layout.addComponents(inner);
        }

        return layout;
    }

    private void selectProfile(ConnectionProfile profile) {
        if (profileSelector != null && profileSelector.containsId(profile)) {
            profileSelector.select(profile);
            profileSelector.setCurrentPageFirstItemId(profile);
        }
    }

    private void addProfile() {
        getUI().addWindow(new ProfileTypeSelectorDialog(new ProfileTypeSelectorDialog.SelectionListener() {
            @Override
            public void onFactorySelected(ConnectionProfileSupportService factory) {
                ConnectionProfile profile = factory.createConnectionProfile();
                ProfileSettingsDialog.edit(profile, databaseSessionManager, new ProfileSettingsDialog.Callback() {
                    @Override
                    public void profileUpdated(ConnectionProfile profile) {
                        SettingsManager settingsManager = SettingsManager.get();
                        settingsManager.getConfiguration().addProfile(profile);
                        settingsManager.persistConfiguration();
                        recreateRootLayout();
                        selectProfile(profile);
                    }
                });
            }
        }));

    }

    private void removeProfile(final ConnectionProfile profile) {
        ConfirmDialog.confirmYesNo("Remove profile \"" + profile.getName() + "\"?", "Remove", "Cancel", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsManager settingsManager = SettingsManager.get();
                settingsManager.getConfiguration().removeProfile(profile);
                settingsManager.persistConfiguration();
                recreateRootLayout();
            }
        });
    }

    private void editProfile(ConnectionProfile profile) {
        ProfileSettingsDialog.edit(profile, databaseSessionManager, new ProfileSettingsDialog.Callback() {
            @Override
            public void profileUpdated(ConnectionProfile profile) {
                SettingsManager.get().persistConfiguration();
                recreateRootLayout();
                selectProfile(profile);
            }
        });
    }

    private static class ProfileInfoPanelHolder extends Panel {

        private ConnectionProfileLoginPanelFactory panelFactory;

        public ProfileInfoPanelHolder() {
            setSizeFull();
            addStyleName(ValoTheme.PANEL_BORDERLESS);
        }

        public void setPanelFactory(ConnectionProfileLoginPanelFactory panelFactory) {
            this.panelFactory = panelFactory;
            setContent(panelFactory.createCompositionRoot());
        }

        public ConnectionProfileLoginPanelFactory getPanelFactory() {
            return panelFactory;
        }

        public void focus() {
            if (getContent() instanceof Focusable) {
                ((Focusable) getContent()).focus();
            }
        }
    }
}
