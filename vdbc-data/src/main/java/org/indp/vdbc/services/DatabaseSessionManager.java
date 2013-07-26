package org.indp.vdbc.services;

import org.indp.vdbc.ConnectionListener;
import org.indp.vdbc.exceptions.InvalidProfileException;
import org.indp.vdbc.model.DataSourceAdapter;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class DatabaseSessionManager implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSessionManager.class);
    private List<DatabaseSession> sessions = new LinkedList<>();
    private final ConnectionListener connectionListener;

    public DatabaseSessionManager(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public DatabaseSession createSession(ConnectionProfile profile) throws InvalidProfileException {
        DataSourceAdapter adapter = profile.createDataSourceAdapter();
        adapter.validateProfile();
        DatabaseSession session = new DatabaseSession(profile, connectionListener);
        sessions.add(session);
        connectionListener.connectionEstablished(session);
        return session;
    }

    public void close() {
        for (DatabaseSession session : sessions) {
            if (!session.isClosed()) {
                session.close();
            }
        }
    }

    public void validateProfile(ConnectionProfile profile) throws InvalidProfileException {
        profile.createDataSourceAdapter().validateProfile();
    }
}
