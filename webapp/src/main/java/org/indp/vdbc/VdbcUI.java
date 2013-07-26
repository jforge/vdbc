package org.indp.vdbc;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.ConnectionSelectorView;
import org.indp.vdbc.ui.WorkspaceView;

/**
 *
 */
@Push
@Theme("vdbc")
@PreserveOnRefresh
public class VdbcUI extends UI implements ConnectionListener {
    public static final String APPLICATION_TITLE = "VDBC";
    //    private static final Logger LOG = LoggerFactory.getLogger(VdbcUI.class);
    private DatabaseSessionManager databaseSessionManager;
    private boolean closing = false;

    @Override
    protected void init(VaadinRequest request) {
        databaseSessionManager = new DatabaseSessionManager(this);
        setContent(createConnectionSelectorView());
    }

    @Override
    public void close() {
        closing = true;
        databaseSessionManager.close();
        super.close();
    }

    @Override
    public void connectionEstablished(DatabaseSession databaseSession) {
        setContent(new WorkspaceView(databaseSession));
        getPage().setTitle(databaseSession.getConnectionProfile().getName() + " - " + APPLICATION_TITLE);
    }

    @Override
    public void connectionClosed(DatabaseSession databaseSession) {
        if (!closing) {
            setContent(createConnectionSelectorView());
            getPage().setTitle(APPLICATION_TITLE);
        }
    }

    private Component createConnectionSelectorView() {
        return new ConnectionSelectorView(databaseSessionManager);
    }
}
