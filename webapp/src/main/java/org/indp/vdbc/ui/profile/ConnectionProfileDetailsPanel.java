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
    }

    @Override
    public void attach() {
        super.attach();
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);

        Component panel = getDetailsComponent();
        Component footer = createFooter();
        layout.addComponent(panel);
        layout.addComponent(footer);
        layout.setExpandRatio(panel, 1);
    }

    private Component createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(new Button("Apply", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    apply();
                } catch (Exception e) {
                    if (e instanceof ErrorMessage) {
                        getWindow().showNotification("Check required values", Window.Notification.TYPE_WARNING_MESSAGE);
                    }
                }
            }
        }));
        footer.addComponent(new Button("Remove", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConnectionProfile value = profileListFacade.getSelectedProfile();
                if (value != null) {
                    removeProfile(value);
                } else {
                    getWindow().showNotification("Select some profile to remove it");
                }
            }
        }));
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
