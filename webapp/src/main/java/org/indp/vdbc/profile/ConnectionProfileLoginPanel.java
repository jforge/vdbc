package org.indp.vdbc.profile;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 */
public abstract class ConnectionProfileLoginPanel<T extends ConnectionProfile> extends CustomComponent {

    public ConnectionProfileLoginPanel(ConnectionProfile profile) {
        setCompositionRoot(new Label("Profile properties are temporarily disabled. Come back soon."));
    }
}
