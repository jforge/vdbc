package org.indp.vdbc.ui.profile;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConnectionProfileDetailsPanel<T extends ConnectionProfile> extends CustomComponent {

    private static final Logger log = LoggerFactory.getLogger(ConnectionProfileDetailsPanel.class);

    private final T profile;
    private final ProfileEditorEvents profileEditorEvents;

    public ConnectionProfileDetailsPanel(T profile, ProfileEditorEvents profileEditorEvents) {
        this.profile = profile;
        this.profileEditorEvents = profileEditorEvents;
    }

    protected abstract Component getDetailsComponent();

    protected abstract void apply();

    public interface ProfileEditorEvents {

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
        Button applyButton = new Button("Apply", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    apply();
                    profileEditorEvents.profileUpdated(profile);
                    Notification.show("Changes applied", "Save profiles to persist settings for further use.", Notification.Type.TRAY_NOTIFICATION);
                } catch (Exception e) {
                    log.debug(e.getMessage(), e);
                    Notification.show("Check required values", Notification.Type.WARNING_MESSAGE);
                }
            }
        });

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.addComponent(applyButton);
        footer.setWidth("100%");
        return footer;
    }

    protected final T getProfile() {
        return profile;
    }
}
