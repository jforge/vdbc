package org.indp.vdbc.ui;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.explorer.TablesView;
import org.indp.vdbc.ui.metadata.DatabaseMetadataView;
import org.indp.vdbc.ui.query.QueryExecutorComponent;
import org.indp.vdbc.util.UnsafeRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class WorkspaceView extends VerticalLayout {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceView.class);
    private TabSheet tabs;

    public WorkspaceView(final DatabaseSession databaseSession) {
        ConnectionProfile profile = databaseSession.getConnectionProfile();

        Button disconnectButton = new Button("Disconnect", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                databaseSession.close();
            }
        });
        disconnectButton.addStyleName(BaseTheme.BUTTON_LINK);

        Toolbar toolbar = new Toolbar();
        toolbar.setSpacing(true);
        toolbar.addLinkButton("Query", new UnsafeRunnable() {
            @Override
            public void run() throws Exception {
                addPage(new QueryExecutorComponent(databaseSession));
            }
        });
        toolbar.addLinkButton("Tables", new UnsafeRunnable() {
            @Override
            public void run() throws Exception {
                addPage(new TablesView(databaseSession));
            }
        });
        toolbar.addLinkButton("Database Overview", new UnsafeRunnable() {
            @Override
            public void run() throws Exception {
                addPage(new DatabaseMetadataView(databaseSession));
            }
        });

        Label titleLabel = new Label(profile.getConnectionPresentationString());
        HorizontalLayout infoBar = new HorizontalLayout(titleLabel, disconnectButton);
        infoBar.setWidth("100%");
        infoBar.setComponentAlignment(titleLabel, Alignment.MIDDLE_LEFT);
        infoBar.setComponentAlignment(disconnectButton, Alignment.MIDDLE_RIGHT);

        String color = databaseSession.getConnectionProfile().getColor();
        if (color != null && !color.isEmpty()) {
            String className = "vdbc-header";
            Page.getCurrent().getStyles().add("." + className + " {background-color:#" + color + ";}");
            infoBar.addStyleName(className);
        }

        addComponents(infoBar, toolbar);
        setWidth("100%");
    }

    private void addPage(Component component) {
        if (tabs == null) {
            createTabSheet();
        }
        TabSheet.Tab tab = tabs.addTab(component);
        tab.setClosable(true);
        tabs.setSelectedTab(tab);
    }

    private void createTabSheet() {
        tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.setCloseHandler(new TabSheet.CloseHandler() {
            @Override
            public void onTabClose(TabSheet tabsheet, Component component) {
                if (component instanceof Closeable) {
                    try {
                        ((Closeable) component).close();
                    } catch (IOException e) {
                        log.warn("failed to call close()", e);
                    }
                }
                tabsheet.removeComponent(component);
                if (tabsheet.getComponentCount() == 0) {
                    removeTabSheet();
                }
            }
        });

        addComponent(tabs);
        setExpandRatio(tabs, 1);
        setSizeFull();
    }

    private void removeTabSheet() {
        removeComponent(tabs);
        setHeight(null);
        tabs = null;
    }
}
