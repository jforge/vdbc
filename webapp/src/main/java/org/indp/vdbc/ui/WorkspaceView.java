package org.indp.vdbc.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.ConnectionListener;
import org.indp.vdbc.DatabaseSessionManager;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.ui.explorer.TablesView;
import org.indp.vdbc.ui.metadata.DatabaseMetadataView;
import org.indp.vdbc.ui.query.QueryExecutorView;

/**
 *
 *
 */
public class WorkspaceView extends VerticalLayout {

    private TabSheet tabSheet;

    public WorkspaceView(final DatabaseSessionManager manager, final ConnectionListener connectionListener) {
        ConnectionProfile profile = manager.getConnectionProfile();
        setSizeFull();

        HorizontalLayout infoBar = new HorizontalLayout();
        infoBar.setWidth(100, UNITS_PERCENTAGE);
//        infoBar.setMargin(false, true, false, true);

        Button disconnectButton = new Button("Disconnect", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                manager.disconnect();
                connectionListener.connectionClosed();
            }
        });
        disconnectButton.setStyleName(BaseTheme.BUTTON_LINK);

        infoBar.addComponent(new Label(profile.getUrl() + " as " + profile.getUser()));
        infoBar.addComponent(disconnectButton);
        infoBar.setComponentAlignment(disconnectButton, Alignment.MIDDLE_RIGHT);

        Toolbar toolbar = new Toolbar();
        toolbar.setSpacing(true);
        toolbar.addLinkButton("Query", null, new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addTab(new QueryExecutorView(manager));
            }
        });
        toolbar.addLinkButton("Tables", null, new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addTab(new TablesView(manager));
            }
        });
        toolbar.addLinkButton("Database Metadata", null, new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addTab(new DatabaseMetadataView(manager));
            }
        });

        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addStyleName(Reindeer.TABSHEET_HOVER_CLOSABLE);

        addComponent(infoBar);
        addComponent(toolbar);
        addComponent(tabSheet);
        setExpandRatio(tabSheet, 1);
    }

    protected void addTab(Component component) {
        Tab tab = tabSheet.addTab(component);
        tab.setClosable(true);
        tabSheet.setSelectedTab(component);
    }
}
