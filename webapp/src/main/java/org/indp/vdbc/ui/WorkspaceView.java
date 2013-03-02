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
import org.indp.vdbc.ui.query.QueryExecutorComponent;

/**
 *
 *
 */
public class WorkspaceView extends VerticalLayout {

    private TabSheet tabSheet;

    public WorkspaceView(final DatabaseSession databaseSession) {
        ConnectionProfile profile = databaseSession.getConnectionProfile();

        Button disconnectButton = new Button("Disconnect", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                databaseSession.close();
            }
        });
        disconnectButton.setStyleName(BaseTheme.BUTTON_LINK);

        Toolbar toolbar = new Toolbar();
        toolbar.setSpacing(true);
        toolbar.addLinkButton("Query", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addTab(new QueryExecutorComponent(databaseSession));
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

        HorizontalLayout infoBar = new HorizontalLayout(
                new Label(profile.getConnectionPresentationString()),
                disconnectButton);
        infoBar.setWidth("100%");
//        infoBar.setMargin(false, true, false, true);
        infoBar.setComponentAlignment(disconnectButton, Alignment.MIDDLE_RIGHT);

        setSizeFull();
        addComponents(infoBar, toolbar, tabSheet);
        setExpandRatio(tabSheet, 1);
    }

    protected void addTab(Component component) {
        Tab tab = tabSheet.addTab(component);
        tab.setClosable(true);
        tabSheet.setSelectedTab(component);
    }
}
