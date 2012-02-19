package org.indp.vdbc;

import com.vaadin.Application;
import com.vaadin.ui.Window;
import org.indp.vdbc.model.config.ConnectionProfile;
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

    @Override
    public void init() {
        setTheme("vdbc");
        databaseSessionManager = new DatabaseSessionManager();
        connectionSelectorView = new ConnectionSelectorView(databaseSessionManager, this);
        setMainWindow(new Window(APPLICATION_TITLE, connectionSelectorView));
    }

    @Override
    public void close() {
        databaseSessionManager.destroy();
        super.close();
    }

    @Override
    public void connectionEstablished(ConnectionProfile connectionProfile) {
        Window w = getMainWindow();
        w.removeAllComponents();
        w.setContent(new WorkspaceView(databaseSessionManager, this));
        w.setCaption(connectionProfile.getConnectionPresentationString() + " - " + APPLICATION_TITLE);
    }

    @Override
    public void connectionClosed() {
        Window w = getMainWindow();
        w.removeAllComponents();
        w.setContent(connectionSelectorView);
        w.setCaption(APPLICATION_TITLE);
    }
}
