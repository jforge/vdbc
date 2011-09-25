package org.indp.vdbc;

import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 * @author pi
 */
public interface ConnectionListener {

    void connectionEstablished(ConnectionProfile connectionProfile);

    void connectionClosed();
}
