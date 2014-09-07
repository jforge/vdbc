package org.indp.vdbc.ui.settings;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileSupportService;

public class ProfileSettingsDialog extends Window {

    private final DatabaseSessionManager databaseSessionManager;
    private final ConnectionProfile profile;
    private final Callback callback;

    public interface Callback {

        void profileUpdated(ConnectionProfile profile);
    }

    public ProfileSettingsDialog(ConnectionProfile profile, DatabaseSessionManager databaseSessionManager, Callback callback) {
        this.profile = profile;
        this.databaseSessionManager = databaseSessionManager;
        this.callback = callback;

        setCaption("Profile Settings");
        setWidth("500px");
        setResizable(false);
        setModal(true);
        setContent(createContent());
    }

    public static void edit(ConnectionProfile profile, DatabaseSessionManager databaseSessionManager, Callback callback) {
        ProfileSettingsDialog dialog = new ProfileSettingsDialog(profile, databaseSessionManager, callback);
        UI.getCurrent().addWindow(dialog);
    }

    private Component createContent() {
        ConnectionProfileSupportService<? extends ConnectionProfile> factory = ConnectionProfileSupportService.Lookup.find(profile.getClass());
        if (factory == null) {
            throw new IllegalStateException();
        }

        final ConnectionProfileDetailsPanel<? extends ConnectionProfile> propertiesPanel = factory.createPropertiesPanel(profile);

        Button okButton = new Button("OK", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    propertiesPanel.apply();
                } catch (Exception e) {
                    Notification.show("Check required fields", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                close();
                callback.profileUpdated(profile);
            }
        });

        Button cancelButton = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

        Button testButton = new Button("Test", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    ConnectionProfile temporaryProfile = propertiesPanel.createTemporaryProfile();
                    databaseSessionManager.validateProfile(temporaryProfile);
                    Notification.show("Connection successful");
                } catch (Exception ex) {
                    Notification.show("Test failed\n", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }

            }
        });

        HorizontalLayout finalButtons = new HorizontalLayout(okButton, cancelButton);
        finalButtons.setSpacing(true);

        HorizontalLayout buttons = new HorizontalLayout(testButton, finalButtons);
        buttons.setComponentAlignment(finalButtons, Alignment.MIDDLE_RIGHT);
        buttons.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        buttons.setWidth("100%");

        VerticalLayout layout = new VerticalLayout(propertiesPanel, buttons);
        layout.setSpacing(true);
        return layout;
    }
}
