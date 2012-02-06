package org.indp.vdbc;

import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 *
 */
public interface ConnectionListener {

    void connectionEstablished(ConnectionProfile connectionProfile);

    void connectionClosed();
}
