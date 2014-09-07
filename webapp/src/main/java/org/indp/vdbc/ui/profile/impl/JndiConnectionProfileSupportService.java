package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JndiConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileDetailsPanel;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanelFactory;
import org.indp.vdbc.ui.profile.ConnectionProfileSupportService;

public class JndiConnectionProfileSupportService implements ConnectionProfileSupportService<JndiConnectionProfile> {

    @Override
    public Class<JndiConnectionProfile> getProfileClass() {
        return JndiConnectionProfile.class;
    }

    @Override
    public JndiConnectionProfile createConnectionProfile() {
        return new JndiConnectionProfile("java:comp/env/");
    }

    @Override
    public ConnectionProfileDetailsPanel<JndiConnectionProfile> createPropertiesPanel(ConnectionProfile profile) {
        assert profile instanceof JndiConnectionProfile;
        return new JndiConnectionProfileDetailsPanel((JndiConnectionProfile) profile);
    }

    @Override
    public ConnectionProfileLoginPanelFactory<JndiConnectionProfile> createLoginPanel(ConnectionProfile profile) {
        assert profile instanceof JndiConnectionProfile;
        return new JndiConnectionProfileLoginPanel((JndiConnectionProfile) profile);
    }

    @Override
    public String getName() {
        return "JNDI";
    }
}
