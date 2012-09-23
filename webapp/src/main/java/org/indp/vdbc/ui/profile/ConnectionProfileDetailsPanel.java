package org.indp.vdbc.ui.profile;

import com.vaadin.terminal.ErrorMessage;
import com.vaadin.ui.*;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.ConfirmDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public abstract class ConnectionProfileDetailsPanel<T extends ConnectionProfile> extends CustomComponent {

    private final T profile;
    private final ProfileListFacade profileListFacade;

    public ConnectionProfileDetailsPanel(T profile, ProfileListFacade profileListFacade) {
        this.profile = profile;
        this.profileListFacade = profileListFacade;
    }

    protected abstract Component getDetailsComponent();

    protected abstract void apply();

    public interface ProfileListFacade {

        ConnectionProfile getSelectedProfile();

        void removeProfile(ConnectionProfile profile);

        void profileUpdated(ConnectionProfile profile);
    }

    @Override
    public void attach() {
        super.attach();
        setSizeFull();

        Component panel = getDetailsComponent();
        Component footer = createFooter();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.addComponent(panel);
        layout.addComponent(footer);
        layout.setExpandRatio(panel, 1);

        setCompositionRoot(layout);
    }

    private Component createFooter() {
        Button removeButton = new Button("Remove Profile", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConnectionProfile value = profileListFacade.getSelectedProfile();
                if (value != null) {
                    removeProfile(value);
                } else {
                    getWindow().showNotification("Select some profile to remove it");
                }
            }
        });
        Button applyButton = new Button("Apply Changes", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    apply();
                    profileListFacade.profileUpdated(profile);
                    getWindow().showNotification("Changes applied", "Save profiles to persist settings for further use.", Window.Notification.TYPE_TRAY_NOTIFICATION);
                } catch (Exception e) {
                    if (e instanceof ErrorMessage) {
                        getWindow().showNotification("Check required values", Window.Notification.TYPE_WARNING_MESSAGE);
                    }
                }
            }
        });

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(removeButton);
        footer.addComponent(applyButton);
        return footer;
    }

    private void removeProfile(final ConnectionProfile profile) {
        ConfirmDialog.confirmYesNo(getApplication(), "Delete profile?", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsManager.get().getConfiguration().removeProfile(profile);
                profileListFacade.removeProfile(profile);
            }
        });
    }

    protected final T getProfile() {
        return profile;
    }
}
