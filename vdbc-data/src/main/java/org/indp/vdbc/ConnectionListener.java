package org.indp.vdbc;

import org.indp.vdbc.services.DatabaseSession;

/**
 *
 *
 */
public interface ConnectionListener {

    void connectionEstablished(DatabaseSession databaseSession);

    void connectionClosed(DatabaseSession databaseSession);
}
