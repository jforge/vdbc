package org.indp.vdbc;

import com.google.common.base.Strings;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.ConnectionSelectorComponent;
import org.indp.vdbc.ui.WorkspaceView;

@Push
@Theme("vdbc")
@PreserveOnRefresh
public class VdbcUI extends UI implements ConnectionListener {

    public static final String APPLICATION_TITLE = "VDBC";
    public static final String ACE_EDITOR_PATH = "/vdbc-webapp/static/ace";
    
    private DatabaseSessionManager databaseSessionManager;

    @Override
    protected void init(VaadinRequest request) {
        getLoadingIndicatorConfiguration().setFirstDelay(50);
        getLoadingIndicatorConfiguration().setSecondDelay(150);
        getLoadingIndicatorConfiguration().setThirdDelay(500);
        getPage().setTitle(APPLICATION_TITLE);
        databaseSessionManager = new DatabaseSessionManager(this);
        setContent(createConnectionSelectorView());
    }

    @Override
    public void close() {
        super.close();
        databaseSessionManager.close();
    }

    @Override
    public void connectionEstablished(DatabaseSession databaseSession) {
        setContent(new WorkspaceView(databaseSession));
        String profileName = databaseSession.getConnectionProfile().getName();
        getPage().setTitle(Strings.isNullOrEmpty(profileName) ? APPLICATION_TITLE : profileName);
    }

    @Override
    public void connectionClosed(DatabaseSession databaseSession) {
        if (!isClosing()) {
            setContent(createConnectionSelectorView());
            getPage().setTitle(APPLICATION_TITLE);
        }
    }

    private Component createConnectionSelectorView() {
        return new ConnectionSelectorComponent(databaseSessionManager);
    }
}
