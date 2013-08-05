package org.indp.vdbc.ui.explorer.details;

import com.google.common.base.Strings;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.J2EEConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.db.DialectSupport;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.util.CustomFreeformQuery;
import org.indp.vdbc.util.ReadonlyFreeformStatementDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TableDataView extends VerticalLayout implements ToolbarContributor {
    private static final Logger LOG = LoggerFactory.getLogger(TableDataView.class);
    public static final String TITLE = "Data";

    private final J2EEConnectionPool connectionPool;
    private final JdbcTable table;
    private final DatabaseSession databaseSession;
    private VerticalLayout tableContainer;
    private HorizontalLayout toolbar;

    public TableDataView(final JdbcTable table, final DatabaseSession databaseSession) {
        this.table = table;
        this.databaseSession = databaseSession;
        connectionPool = new J2EEConnectionPool(databaseSession.getDataSource());

        toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        Button refreshButton = new Button("Refresh", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                refreshDataView(table, databaseSession);
            }

        });
        refreshButton.setStyleName(Reindeer.BUTTON_SMALL);
        toolbar.addComponent(refreshButton);
        toolbar.setComponentAlignment(refreshButton, Alignment.MIDDLE_RIGHT);

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

    protected void refreshDataView(JdbcTable tableDefinition, DatabaseSession databaseSession) {
        Component component;
        try {
            final String tableName = createTableName(tableDefinition, databaseSession);
            final String queryString = databaseSession.getDialect().getExpressions().selectAllFromTable(tableName);

            final FreeformQuery query;

            // todo StatementDelegateFactory
            // todo pk columns
            if (databaseSession.getDialect().supportsLimitedSelects()) {
                query = new CustomFreeformQuery(queryString, connectionPool);
                query.setDelegate(new ReadonlyFreeformStatementDelegate(tableName, databaseSession));
            } else {
                query = new FreeformQuery(queryString, connectionPool);
                Notification.show(
                        "Warning!",
                        "Using slow mode because dialect doesn't\nsupport limit/offset select queries.",
                        Notification.Type.TRAY_NOTIFICATION);
            }

            SQLContainer container = new SQLContainer(query);
            final Table table = new Table(null, container);
            table.setPageLength(100); // todo configure
            table.setSelectable(true);
            table.setSortEnabled(false);
            table.setColumnReorderingAllowed(true);
            table.setColumnCollapsingAllowed(true);
            table.setNullSelectionAllowed(false);
            table.setSizeFull();
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
            panel.addStyleName(Reindeer.PANEL_LIGHT);
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

    protected String createTableName(JdbcTable table, DatabaseSession databaseSession) throws SQLException {
        DatabaseMetaData metaData = databaseSession.getMetadata().getRawMetadata();
        StringBuilder sb = new StringBuilder();
        if (!Strings.isNullOrEmpty(table.getCatalog()) && metaData.supportsCatalogsInTableDefinitions()) {
            sb.append(table.getCatalog()).append(".");
        }
        if (!Strings.isNullOrEmpty(table.getSchema()) && metaData.supportsSchemasInTableDefinitions()) {
            sb.append(table.getSchema()).append(".");
        }
        sb.append(table.getName());
        return sb.toString();
    }

    @Override
    public Component getToolbar() {
        return toolbar;
    }
}
