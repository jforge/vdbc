package org.indp.vdbc.ui;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import org.indp.vdbc.ConnectionListener;
import org.indp.vdbc.DatabaseSessionManager;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.settings.SettingsManagerView;
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

        Panel panel = new Panel("DB Console");
        panel.setWidth(300, UNITS_PIXELS);
        addComponent(panel);
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        FormLayout l = new FormLayout();
        panel.addComponent(l);
        l.setSizeFull();
        l.setSpacing(true);
        l.setMargin(false, false, true, false);

        final LabelField driver = new LabelField();
        final LabelField url = new LabelField();
        final TextField userName = new TextField();
        final PasswordField password = new PasswordField();

        List<ConnectionProfile> profilesList = SettingsManager.get().getConfiguration().getProfiles();
        final ComboBox profiles = new ComboBox(null, profilesList);
        profiles.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                ConnectionProfile cp = (ConnectionProfile) profiles.getValue();
                driver.setValue(cp.getDriver());
                url.setValue(cp.getUrl());
                url.setDescription(cp.getUrl());
                userName.setValue(cp.getUser());
            }
        });

        profiles.setImmediate(true);
        profiles.setNewItemsAllowed(false);
        profiles.setNullSelectionAllowed(false);

        if (!profilesList.isEmpty()) {
            profiles.select(profilesList.get(0));
        }

        addToForm(l, "Profile:", profiles);
        addToForm(l, "Driver:", driver);
        addToForm(l, "URL:", url);
        addToForm(l, "Username:", userName);
        addToForm(l, "Password:", password);

        Button connectButton = new Button("Connect", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ConnectionProfile connectionProfile = new ConnectionProfile(null,
                        driver.getValue().toString(), url.getValue().toString(),
                        userName.getValue().toString(), password.getValue().toString());
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
                    databaseSessionManager.test(driver.getValue().toString(), url.getValue().toString(), userName.getValue().toString(), password.getValue().toString());
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
                SettingsManagerView settingsManagerView = new SettingsManagerView();
                getWindow().addWindow(settingsManagerView);
                getWindow().showNotification("Stay tuned: settings management is work in progress", Notification.TYPE_WARNING_MESSAGE);
            }
        });

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth(100, UNITS_PERCENTAGE);
        panel.addComponent(hl);
        hl.addComponent(settingsButton);
        hl.addComponent(testButton);
        hl.addComponent(connectButton);
        hl.setComponentAlignment(testButton, Alignment.MIDDLE_RIGHT);
        hl.setComponentAlignment(connectButton, Alignment.MIDDLE_RIGHT);

        userName.focus();

        super.attach();
    }

    private void addToForm(FormLayout grid, String title, Component component) {
        component.setWidth("100%");
        component.setCaption(title);
        grid.addComponent(component);
    }
}
