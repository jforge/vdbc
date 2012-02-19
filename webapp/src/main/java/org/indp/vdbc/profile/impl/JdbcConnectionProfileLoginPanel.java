package org.indp.vdbc.profile.impl;

import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.profile.ConnectionProfileLoginPanel;

/**
 *
 */
public class JdbcConnectionProfileLoginPanel<T extends ConnectionProfile> extends ConnectionProfileLoginPanel<T> {

    public JdbcConnectionProfileLoginPanel(ConnectionProfile profile) {
        super(profile);
    }
}
