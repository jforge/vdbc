package org.indp.vdbc;

import com.vaadin.Application;
import com.vaadin.ui.Window;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.ConnectionSelectorView;
import org.indp.vdbc.ui.WorkspaceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class VdbcApplication extends Application implements ConnectionListener {

    public static final String APPLICATION_TITLE = "DB Console";
    private static final Logger LOG = LoggerFactory.getLogger(VdbcApplication.class);
    private DatabaseSessionManager databaseSessionManager;
    private ConnectionSelectorView connectionSelectorView;
    private boolean closing = false;

    @Override
    public void init() {
        setTheme("vdbc");
        databaseSessionManager = new DatabaseSessionManager(this);
        connectionSelectorView = new ConnectionSelectorView(databaseSessionManager);
        setMainWindow(new Window(APPLICATION_TITLE, connectionSelectorView));
    }

    @Override
    public void close() {
        closing = true;
        databaseSessionManager.close();
        super.close();
    }

    @Override
    public void connectionEstablished(DatabaseSession databaseSession) {
        Window w = getMainWindow();
        w.removeAllComponents();
        w.setContent(new WorkspaceView(databaseSession));
        w.setCaption(databaseSession.getConnectionProfile().getConnectionPresentationString() + " - " + APPLICATION_TITLE);
    }

    @Override
    public void connectionClosed(DatabaseSession databaseSession) {
        if (!closing) {
            Window w = getMainWindow();
            w.removeAllComponents();
            w.setContent(connectionSelectorView);
            w.setCaption(APPLICATION_TITLE);
        }
    }
}
