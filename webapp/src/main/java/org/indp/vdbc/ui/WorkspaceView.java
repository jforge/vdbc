package org.indp.vdbc.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.explorer.TablesView;
import org.indp.vdbc.ui.metadata.DatabaseMetadataView;
import org.indp.vdbc.ui.query.QueryExecutorComponent;
import org.indp.vdbc.util.UnsafeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

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

        MenuBar menuBar = createMenu(databaseSession);
        Label titleLabel = new Label(profile.getConnectionPresentationString());

        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        leftSide.addComponents(menuBar, titleLabel);

        HorizontalLayout top = new HorizontalLayout();

        top.addComponents(leftSide, disconnectButton);
        top.setComponentAlignment(leftSide, Alignment.MIDDLE_LEFT);
        top.setComponentAlignment(disconnectButton, Alignment.MIDDLE_RIGHT);

        String color = databaseSession.getConnectionProfile().getColor();
        if (color != null && !color.isEmpty()) {
            String className = "vdbc-header";
            Page.getCurrent().getStyles().add("." + className + " {background-color:#" + color + ";}");
            top.addStyleName(className);
        }

        top.setWidth("100%");

        addComponent(top);

        addAttachListener(new AttachListener() {
            @Override
            public void attach(AttachEvent event) {
                try {
                    addPage(new QueryExecutorComponent(databaseSession));
                } catch (SQLException ignored) {
                }
            }
        });
    }

    private MenuBar createMenu(final DatabaseSession databaseSession) {
        MenuBar menuBar = new MenuBar();
        menuBar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        menuBar.addStyleName(ValoTheme.MENUBAR_SMALL);
        menuBar.setSizeUndefined();
        MenuBar.MenuItem rootItem = menuBar.addItem("", FontAwesome.BARS, null);
        rootItem.addItem("Query", new UnsafeCommand() {
            @Override
            public void menuSelectedImpl(MenuBar.MenuItem selectedItem) throws Exception {
                addPage(new QueryExecutorComponent(databaseSession));
            }
        });
        rootItem.addItem("Tables", new UnsafeCommand() {
            @Override
            public void menuSelectedImpl(MenuBar.MenuItem selectedItem) throws Exception {
                addPage(new TablesView(databaseSession));
            }
        });
        rootItem.addItem("Database Overview", new UnsafeCommand() {
            @Override
            public void menuSelectedImpl(MenuBar.MenuItem selectedItem) throws Exception {
                addPage(new DatabaseMetadataView(databaseSession));
            }
        });
        return menuBar;
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
        tabs.addStyleName(ValoTheme.TABSHEET_FRAMED);
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
