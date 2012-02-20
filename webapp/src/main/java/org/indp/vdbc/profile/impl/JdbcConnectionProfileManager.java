package org.indp.vdbc.profile.impl;

import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.profile.ConnectionProfileLoginPanel;
import org.indp.vdbc.profile.ConnectionProfileManager;

/**
 *
 */
public class JdbcConnectionProfileManager implements ConnectionProfileManager<JdbcConnectionProfile> {

    @Override
    public Class<JdbcConnectionProfile> getProfileClass() {
        return JdbcConnectionProfile.class;
    }

    @Override
    public JdbcConnectionProfile createConnectionProfile() {
        return new JdbcConnectionProfile();
    }

    @Override
    public ConnectionProfileDetailsPanel<JdbcConnectionProfile> createPropertiesPanel(ConnectionProfile profile, ConnectionProfileDetailsPanel.ProfileListFacade profileListFacade) {
        return new JdbcConnectionProfilePanel((JdbcConnectionProfile) profile, profileListFacade);
    }

    @Override
    public ConnectionProfileLoginPanel<JdbcConnectionProfile> createLoginPanel(ConnectionProfile profile) {
        assert profile instanceof JdbcConnectionProfile;
        return new JdbcConnectionProfileLoginPanel<JdbcConnectionProfile>((JdbcConnectionProfile) profile);
    }

    @Override
    public String getName() {
        return "JDBC";
    }
}
