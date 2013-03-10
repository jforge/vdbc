package org.indp.vdbc.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
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

    private Component detailsComponent;

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
                addView(new QueryExecutorComponent(databaseSession));
            }
        });
        toolbar.addLinkButton("Tables", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addView(new TablesView(databaseSession));
            }
        });
        toolbar.addLinkButton("Database Metadata", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addView(new DatabaseMetadataView(databaseSession));
            }
        });

        HorizontalLayout infoBar = new HorizontalLayout(
                new Label(profile.getConnectionPresentationString()),
                disconnectButton);
        infoBar.setWidth("100%");
//        infoBar.setMargin(false, true, false, true);
        infoBar.setComponentAlignment(disconnectButton, Alignment.MIDDLE_RIGHT);

        setSizeFull();
        addComponents(infoBar, toolbar);
        addView(new Label());
    }

    protected void addView(Component component) {
        if (detailsComponent != null) {
            replaceComponent(detailsComponent, component);
        } else {
            addComponent(component);
        }
        detailsComponent = component;
        setExpandRatio(detailsComponent, 1);
    }
}
