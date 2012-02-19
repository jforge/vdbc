package org.indp.vdbc.ui;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import org.indp.vdbc.ConnectionListener;
import org.indp.vdbc.DatabaseSessionManager;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.profile.ConnectionProfileLoginPanel;
import org.indp.vdbc.profile.ConnectionProfileManager;
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
    private final ConnectionListener connectionListener;

    public ConnectionSelectorView(DatabaseSessionManager databaseSessionManager, ConnectionListener connectionListener) {
        this.databaseSessionManager = databaseSessionManager;
        this.connectionListener = connectionListener;
    }

    @Override
    public void attach() {
        setSizeFull();

        Panel rootPanel = new Panel("DB Console");
        rootPanel.setWidth(300, UNITS_PIXELS);
        addComponent(rootPanel);
        setComponentAlignment(rootPanel, Alignment.MIDDLE_CENTER);

//        FormLayout l = new FormLayout();
//        panel.addComponent(l);
//        l.setSizeFull();
//        l.setSpacing(true);
//        l.setMargin(false, false, true, false);
//        final LabelField driver = new LabelField();
//        final LabelField url = new LabelField();
//        final TextField userName = new TextField();
//        final PasswordField password = new PasswordField();

        final Panel profileInfoPanel = new Panel();
        profileInfoPanel.setWidth("100%");

        List<ConnectionProfile> profileList = SettingsManager.get().getConfiguration().getProfiles();
        final ComboBox profiles = new ComboBox(null,
                new BeanItemContainer<ConnectionProfile>(
                        ConnectionProfile.class, SettingsManager.get().getConfiguration().getProfiles()));
        profiles.setWidth("100%");
        profiles.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        profiles.setItemCaptionPropertyId("name");
        profiles.setImmediate(true);
        profiles.setNullSelectionAllowed(false);
        profiles.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                ConnectionProfile cp = (ConnectionProfile) profiles.getValue();
                if (cp == null) {
                    return;
                }
                ConnectionProfileManager<? extends ConnectionProfile> connectionProfileManager = ConnectionProfileManager.Lookup.find(cp.getClass());
                ConnectionProfileLoginPanel<? extends ConnectionProfile> loginPanel = connectionProfileManager.createLoginPanel(cp);
                profileInfoPanel.removeAllComponents();
                profileInfoPanel.addComponent(loginPanel);
                // todo!!
//                driver.setValue(cp.getDriver());
//                url.setValue(cp.getUrl());
//                url.setDescription(cp.getUrl());
//                userName.setValue(cp.getUser());
            }
        });

        if (!profileList.isEmpty()) {
            profiles.select(profileList.get(0));
        }

        rootPanel.addComponent(profiles);
        rootPanel.addComponent(profileInfoPanel);

//        addToForm(l, "Profile:", profiles);
//        addToForm(l, "Driver:", driver);
//        addToForm(l, "URL:", url);
//        addToForm(l, "Username:", userName);
//        addToForm(l, "Password:", password);

        Button connectButton = new Button("Connect", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ConnectionProfile selectedProfile = (ConnectionProfile) profiles.getValue();
                ConnectionProfile connectionProfile = null;
                try {
                    connectionProfile = selectedProfile.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    getWindow().showNotification(e.getMessage());
                    return;
                }
                // todo!!
//                        new ConnectionProfile(null,
//                        driver.getValue().toString(), url.getValue().toString(),
//                        userName.getValue().toString(), password.getValue().toString());
                try {
                    databaseSessionManager.connect(connectionProfile);
                    connectionListener.connectionEstablished(connectionProfile);
                } catch (Exception ex) {
                    getApplication().getMainWindow().showNotification("Failed to connect<br/>", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        Button testButton = new Button("Test", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    ConnectionProfile selectedProfile = (ConnectionProfile) profiles.getValue();
                    // todo!!
                    databaseSessionManager.isValidProfile(selectedProfile);
                    getApplication().getMainWindow().showNotification("Test successful");
                } catch (Exception ex) {
                    LOG.warn("connection test failed", ex);
                    getApplication().getMainWindow().showNotification("Test failed<br/>", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        Button settingsButton = new Button("Settings...", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                SettingsManagerDialog settingsManagerDialog = new SettingsManagerDialog();
                settingsManagerDialog.addListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(Window.CloseEvent e) {
                        List<ConnectionProfile> list = SettingsManager.get().getConfiguration().getProfiles();
                        profiles.setContainerDataSource(new BeanItemContainer<ConnectionProfile>(ConnectionProfile.class, list));
                        if (!list.isEmpty()) {
                            profiles.select(list.get(0));
                        }
                    }
                });
                getWindow().addWindow(settingsManagerDialog);
            }
        });

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth(100, UNITS_PERCENTAGE);
        rootPanel.addComponent(hl);
        hl.addComponent(settingsButton);
        hl.addComponent(testButton);
        hl.addComponent(connectButton);
        hl.setComponentAlignment(testButton, Alignment.MIDDLE_RIGHT);
        hl.setComponentAlignment(connectButton, Alignment.MIDDLE_RIGHT);

        // todo!!
//        userName.focus();

        super.attach();
    }

//    private void addToForm(FormLayout grid, String title, Component component) {
//        component.setWidth("100%");
//        component.setCaption(title);
//        grid.addComponent(component);
//    }
}
