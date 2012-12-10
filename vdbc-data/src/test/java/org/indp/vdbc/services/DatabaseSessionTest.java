package org.indp.vdbc.services;

import org.indp.vdbc.ConnectionListener;
import org.indp.vdbc.exceptions.InvalidProfileException;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class DatabaseSessionTest {
    private DatabaseSessionManager sessionManager;
    private DatabaseSession session;

    @Test
    public void test() throws Exception {
    }

    @Before
    public void setup() throws InvalidProfileException {
        JdbcConnectionProfile profile = new JdbcConnectionProfile("test", "h2", "org.h2.Driver", "jdbc:h2:mem:" + System.currentTimeMillis(), "sa", "1");

        sessionManager = new DatabaseSessionManager(new ConnectionListener() {
            @Override
            public void connectionEstablished(DatabaseSession databaseSession) {
                System.out.println("connectionEstablished");
            }

            @Override
            public void connectionClosed(DatabaseSession databaseSession) {
                System.out.println("connectionClosed");
            }
        });

        session = sessionManager.createSession(profile);
    }

    @After
    public void tearDown() {
        sessionManager.close();
    }
}
