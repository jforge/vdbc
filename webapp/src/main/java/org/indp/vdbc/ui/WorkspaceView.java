package org.indp.vdbc.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.explorer.TablesView;
import org.indp.vdbc.ui.metadata.DatabaseMetadataView;
import org.indp.vdbc.ui.query.QueryExecutorView;

/**
 *
 *
 */
public class WorkspaceView extends VerticalLayout {

    private TabSheet tabSheet;

    public WorkspaceView(final DatabaseSession databaseSession) {
        ConnectionProfile profile = databaseSession.getConnectionProfile();
        setSizeFull();

        HorizontalLayout infoBar = new HorizontalLayout();
        infoBar.setWidth(100, UNITS_PERCENTAGE);
//        infoBar.setMargin(false, true, false, true);

        Button disconnectButton = new Button("Disconnect", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                databaseSession.close();
            }
        });
        disconnectButton.setStyleName(BaseTheme.BUTTON_LINK);

        infoBar.addComponent(new Label(profile.getConnectionPresentationString()));
        infoBar.addComponent(disconnectButton);
        infoBar.setComponentAlignment(disconnectButton, Alignment.MIDDLE_RIGHT);

        Toolbar toolbar = new Toolbar();
        toolbar.setSpacing(true);
        toolbar.addLinkButton("Query", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addTab(new QueryExecutorView(databaseSession));
            }
        });
        toolbar.addLinkButton("Tables", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addTab(new TablesView(databaseSession));
            }
        });
        toolbar.addLinkButton("Database Metadata", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addTab(new DatabaseMetadataView(databaseSession));
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
