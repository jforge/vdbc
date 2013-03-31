package org.indp.vdbc.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.explorer.TablesView;
import org.indp.vdbc.ui.metadata.DatabaseMetadataView;
import org.indp.vdbc.ui.query.QueryExecutorComponent;
import org.indp.vdbc.ui.workspace.HeaderBarComponent;
import org.indp.vdbc.ui.workspace.WorkspacePageComponent;

/**
 *
 *
 */
public class WorkspaceView extends VerticalLayout {

    private SingleComponentContainer detailsContainer;
    private HeaderBarComponent tabs;

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
                tabs.addPage(new QueryExecutorComponent(databaseSession));
            }
        });
        toolbar.addLinkButton("Tables", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tabs.addPage(new TablesView(databaseSession));
            }
        });
        toolbar.addLinkButton("Database Metadata", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tabs.addPage(new DatabaseMetadataView(databaseSession));
            }
        });

        HorizontalLayout infoBar = new HorizontalLayout(
                new Label(profile.getConnectionPresentationString()),
                disconnectButton);
        infoBar.setWidth("100%");
        infoBar.setComponentAlignment(disconnectButton, Alignment.MIDDLE_RIGHT);

        tabs = new HeaderBarComponent() {
            @Override
            protected void onPageSelection(WorkspacePageComponent component) {
                replaceView(component);
            }

            @Override
            protected void onPageClose(WorkspacePageComponent component) {
            }
        };

        detailsContainer = new Panel();
        detailsContainer.setStyleName(Reindeer.PANEL_LIGHT);
        detailsContainer.addStyleName("page-content-panel");
        detailsContainer.setSizeFull();

        setSizeFull();
        addComponents(infoBar, toolbar, tabs, detailsContainer);
        setExpandRatio(detailsContainer, 1);
    }

    private void replaceView(Component component) {
        Component newContent = component == null ? new Label() : component;
        detailsContainer.setContent(newContent);
    }
}
