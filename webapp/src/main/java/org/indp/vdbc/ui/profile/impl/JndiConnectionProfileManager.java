package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JndiConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileManager;

/**
 *
 */
public class JndiConnectionProfileManager implements ConnectionProfileManager<JndiConnectionProfile> {

    @Override
    public Class<JndiConnectionProfile> getProfileClass() {
        return JndiConnectionProfile.class;
    }

    @Override
    public JndiConnectionProfile createConnectionProfile() {
        return new JndiConnectionProfile("java:comp/env/");
    }

    @Override
    public ConnectionProfileDetailsPanel<JndiConnectionProfile> createPropertiesPanel(ConnectionProfile profile, ConnectionProfileDetailsPanel.ProfileListFacade profileListFacade) {
        assert profile instanceof JndiConnectionProfile;
        return new JndiConnectionProfileDetailsPanel((JndiConnectionProfile) profile, profileListFacade);
    }

    @Override
    public ConnectionProfileLoginPanel<JndiConnectionProfile> createLoginPanel(ConnectionProfile profile) {
        assert profile instanceof JndiConnectionProfile;
        return new JndiConnectionProfileLoginPanel((JndiConnectionProfile) profile);
    }

    @Override
    public String getName() {
        return "JNDI";
    }
}
