package org.indp.vdbc.ui.explorer.details;

import com.google.common.base.Strings;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.J2EEConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.db.DialectSupport;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.UiUtils;
import org.indp.vdbc.util.CustomFreeformQuery;
import org.indp.vdbc.util.ReadonlyFreeformStatementDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDataView extends VerticalLayout implements ToolbarContributor {

    private static final Logger LOG = LoggerFactory.getLogger(TableDataView.class);
    public static final String TITLE = "Data";

    private final J2EEConnectionPool connectionPool;
    private final JdbcTable table;
    private final DatabaseSession databaseSession;
    private VerticalLayout tableContainer;
    private Panel toolbar;

    public TableDataView(final JdbcTable table, final DatabaseSession databaseSession) {
        this.table = table;
        this.databaseSession = databaseSession;
        this.connectionPool = new J2EEConnectionPool(databaseSession.getDataSource());

        final TextField filter = new TextField();
        filter.setWidth("100%");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.addStyleName("monospace");

        Button refreshButton = new Button("Refresh", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                refreshDataView(table, databaseSession, filter.getValue());
            }
        });
        refreshButton.addStyleName(ValoTheme.BUTTON_TINY);

        Label filterLabel = new Label("Filter:&nbsp;", ContentMode.HTML);
        filterLabel.setSizeUndefined();
        filterLabel.addStyleName(ValoTheme.LABEL_TINY);

        HorizontalLayout toolbarLayout = new HorizontalLayout();
        toolbarLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        toolbarLayout.addComponents(filterLabel, filter, refreshButton);
        toolbarLayout.setWidth("100%");
        toolbarLayout.setExpandRatio(filter, 1);
        toolbarLayout.setComponentAlignment(refreshButton, Alignment.MIDDLE_RIGHT);
        toolbarLayout.setMargin(new MarginInfo(false, false, false, true));

        toolbar = new Panel(toolbarLayout);
        toolbar.addStyleName(ValoTheme.PANEL_BORDERLESS);
        toolbar.addShortcutListener(new ShortcutListener("apply filter", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                refreshDataView(table, databaseSession, filter.getValue());
            }
        });

        tableContainer = new VerticalLayout();
        tableContainer.setSizeFull();

        addComponent(tableContainer);
        setExpandRatio(tableContainer, 1f);
        setCaption(TITLE);
        setSizeFull();
    }

    @Override
    public void attach() {
        super.attach();
        refreshDataView(table, databaseSession);
    }

    private void refreshDataView(JdbcTable tableDefinition, DatabaseSession databaseSession) {
        refreshDataView(tableDefinition, databaseSession, null);
    }

    private void refreshDataView(JdbcTable tableDefinition, DatabaseSession databaseSession, String filter) {
        Component component;
        try {
            Container container = createContainer(tableDefinition, databaseSession, filter);
            final Table table = UiUtils.createTable(container);
            table.addActionHandler(new Action.Handler() {
                private final Action viewSingleRecordAction = new Action("Single record view...");

                @Override
                public Action[] getActions(Object target, Object sender) {
                    return new Action[]{viewSingleRecordAction};
                }

                @Override
                public void handleAction(Action action, Object sender, Object target) {
                    if (action == viewSingleRecordAction) {
                        showRecordEditor(table, target);
                    }
                }
            });
            table.setVisibleColumns(filterColumns(table.getVisibleColumns()));

            Panel panel = new Panel(table);
            panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
            panel.setSizeFull();
            panel.addShortcutListener(new ShortcutListener("show record", ShortcutAction.KeyCode.F4, null) {
                @Override
                public void handleAction(Object sender, Object target) {
                    Object value = table.getValue();
                    if (value != null) {
                        showRecordEditor(table, value);
                    }
                }
            });

            component = panel;
        } catch (SQLException e) {
            LOG.warn("failed to retrieve tableDefinition data", e);
            component = new Label(e.getMessage());
        }
        tableContainer.removeAllComponents();
        tableContainer.addComponent(component);
    }

    private Container createContainer(JdbcTable tableDefinition, DatabaseSession databaseSession, String filter) throws SQLException {
        filter = fixFilter(filter);

        final String tableName = databaseSession.buildTableName(tableDefinition);
        final String queryString = databaseSession.getDialect().getExpressions().selectAllFromTable(tableName, filter);

        final FreeformQuery query;

        // todo StatementDelegateFactory
        // todo pk columns
        if (databaseSession.getDialect().supportsLimitedSelects()) {
            query = new CustomFreeformQuery(queryString, connectionPool);
            query.setDelegate(new ReadonlyFreeformStatementDelegate(tableName, filter, databaseSession));
        } else {
            query = new FreeformQuery(queryString, connectionPool);
            Notification.show(
                    "Warning!",
                    "Using slow mode because dialect doesn't\nsupport limit/offset select queries.",
                    Notification.Type.TRAY_NOTIFICATION);
        }

        return new SQLContainer(query);
    }

    private String fixFilter(String filter) {
        if (filter != null) {
            filter = filter.trim();
            if (Strings.isNullOrEmpty(filter)) {
                filter = null;
            }
        }
        return filter;
    }

    private void showRecordEditor(final Table table, Object targetValue) {
        Item item = table.getContainerDataSource().getItem(targetValue);
        if (item != null) {
            getUI().addWindow(new SingleRecordViewWindow(item, new Runnable() {
                @Override
                public void run() {
                    table.focus();
                }
            }));
        }
    }

    private Object[] filterColumns(Object[] visibleColumns) {
        List<Object> list = new ArrayList<>(visibleColumns.length);
        for (Object column : visibleColumns) {
            if (!DialectSupport.isServiceColumn(column.toString())) {
                list.add(column);
            }
        }
        return list.toArray();
    }

    @Override
    public Component getToolbar() {
        return toolbar;
    }
}
