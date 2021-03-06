package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanelFactory;
import org.indp.vdbc.ui.profile.ConnectionProfileSupportService;

public class JdbcConnectionProfileSupportService implements ConnectionProfileSupportService<JdbcConnectionProfile> {

    @Override
    public Class<JdbcConnectionProfile> getProfileClass() {
        return JdbcConnectionProfile.class;
    }

    @Override
    public JdbcConnectionProfile createConnectionProfile() {
        return new JdbcConnectionProfile();
    }

    @Override
    public ConnectionProfileDetailsPanel<JdbcConnectionProfile> createPropertiesPanel(ConnectionProfile profile) {
        return new JdbcConnectionProfileDetailsPanel((JdbcConnectionProfile) profile);
    }

    @Override
    public ConnectionProfileLoginPanelFactory<JdbcConnectionProfile> createLoginPanel(ConnectionProfile profile) {
        assert profile instanceof JdbcConnectionProfile;
        return new JdbcConnectionProfileLoginPanel((JdbcConnectionProfile) profile);
    }

    @Override
    public String getName() {
        return "JDBC";
    }
}
