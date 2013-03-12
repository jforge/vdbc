package org.indp.vdbc.ui;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileManager;
import org.indp.vdbc.ui.settings.SettingsManagerDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 *
 */
public class ConnectionSelectorView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionSelectorView.class);
    private final DatabaseSessionManager databaseSessionManager;

    public ConnectionSelectorView(DatabaseSessionManager databaseSessionManager) {
        this.databaseSessionManager = databaseSessionManager;
    }

    @Override
    public void attach() {
        super.attach();

        final ProfileInfoPanelHolder<ConnectionProfileLoginPanel> profileInfoPanel = new ProfileInfoPanelHolder<ConnectionProfileLoginPanel>();

        List<ConnectionProfile> profileList = SettingsManager.get().getConfiguration().getProfiles();
        final ComboBox profiles = new ComboBox("Profile:",
                new BeanItemContainer<ConnectionProfile>(
                        ConnectionProfile.class, SettingsManager.get().getConfiguration().getProfiles()));
        profiles.setWidth("100%");
        profiles.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        profiles.setItemCaptionPropertyId("name");
        profiles.setImmediate(true);
        profiles.setNullSelectionAllowed(false);
        profiles.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                ConnectionProfile cp = (ConnectionProfile) profiles.getValue();
                if (cp == null) {
                    return;
                }
                ConnectionProfileManager<? extends ConnectionProfile> connectionProfileManager = ConnectionProfileManager.Lookup.find(cp.getClass());
                ConnectionProfileLoginPanel<? extends ConnectionProfile> loginPanel = connectionProfileManager.createLoginPanel(cp);
                profileInfoPanel.setPanel(loginPanel);
                profileInfoPanel.focus();
            }
        });

        if (!profileList.isEmpty()) {
            profiles.select(profileList.get(0));
        }

        Button connectButton = new Button("Connect", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ConnectionProfileLoginPanel panel = profileInfoPanel.getPanel();
                ConnectionProfile profile = panel.createConnectionProfile();
                try {
                    databaseSessionManager.createSession(profile);
                } catch (Exception ex) {
                    Notification.show("Failed to connect", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        connectButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        Button testButton = new Button("Test", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    ConnectionProfile selectedProfile = (ConnectionProfile) profiles.getValue();
                    databaseSessionManager.validateProfile(selectedProfile);
                    Notification.show("Test successful");
                } catch (Exception ex) {
                    LOG.warn("connection test failed", ex);
                    Notification.show("Test failed", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        Button settingsButton = new Button("Settings...");
        String settingsEditorEnabled = System.getProperty("vdbc.settings.editor-enabled");
        if (settingsEditorEnabled != null && !"true".equals(settingsEditorEnabled)) {
            settingsButton.setEnabled(false);
            settingsButton.setDescription("Settings editor is disabled because 'vdbc.settings.editor-enabled' system property is defined and its value is not 'true'.");
        } else {
            settingsButton.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    SettingsManagerDialog settingsManagerDialog = new SettingsManagerDialog();
                    settingsManagerDialog.addCloseListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent e) {
                            List<ConnectionProfile> list = SettingsManager.get().getConfiguration().getProfiles();
                            profiles.setContainerDataSource(new BeanItemContainer<ConnectionProfile>(ConnectionProfile.class, list));
                            if (!list.isEmpty()) {
                                profiles.select(list.get(0));
                            }
                        }
                    });
                    getUI().addWindow(settingsManagerDialog);
                }
            });
        }

        HorizontalLayout hl = new HorizontalLayout(settingsButton, testButton, connectButton);
        hl.setWidth("100%");
        hl.setComponentAlignment(testButton, Alignment.MIDDLE_RIGHT);
        hl.setComponentAlignment(connectButton, Alignment.MIDDLE_RIGHT);

        VerticalLayout rootPanelLayout = new VerticalLayout(profiles, profileInfoPanel, hl);
        rootPanelLayout.setMargin(true);

        Panel rootPanel = new Panel("DB Console");
        rootPanel.setWidth("300px");
        rootPanel.setContent(rootPanelLayout);

        setSizeFull();
        addComponent(rootPanel);
        setComponentAlignment(rootPanel, Alignment.MIDDLE_CENTER);
    }

    protected static class ProfileInfoPanelHolder<T extends Component> extends CustomComponent {

        private final VerticalLayout root;
        private T component;

        public ProfileInfoPanelHolder() {
            root = new VerticalLayout();
            root.setMargin(false);
            root.setWidth("100%");
            setCompositionRoot(root);
        }

        public void setPanel(T component) {
            root.removeAllComponents();
            this.component = component;
            root.addComponent(this.component);
        }

        public T getPanel() {
            return component;
        }

        public void focus() {
            if (component instanceof Focusable) {
                ((Focusable) component).focus();
            }
        }
    }
}
