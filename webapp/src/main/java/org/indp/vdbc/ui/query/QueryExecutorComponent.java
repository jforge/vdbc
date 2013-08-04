package org.indp.vdbc.ui.query;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.ResultSetTable;
import org.indp.vdbc.ui.UiUtils;
import org.indp.vdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class QueryExecutorComponent extends VerticalLayout implements Closeable {
    private static final boolean DEFAULT_AUTO_COMMIT = true;
    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorComponent.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);

    private final Connection connection;
    //
    private VerticalSplitPanel splitPanel;
    private HorizontalLayout queryOptionsLayout;
    private TextArea query;
    private ComboBox maxRowsBox;

    public QueryExecutorComponent(DatabaseSession databaseSession) throws SQLException {
        connection = databaseSession.getConnection();
        connection.setAutoCommit(DEFAULT_AUTO_COMMIT);

        buildQueryTextEditor();
        buildToolbar();

        splitPanel = new VerticalSplitPanel(query, new Label());
        splitPanel.setSizeFull();

        addComponents(queryOptionsLayout, splitPanel);
        setSizeFull();
        setExpandRatio(splitPanel, 1);

        setCaption("Query");
    }

    @Override
    public void close() {
        JdbcUtils.close(connection);
    }

    private void buildToolbar() {
        final Button executeButton = createToolButton("Execute", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                executeQuery(query.getValue());
            }
        });
        executeButton.setDescription("Press Ctrl+Enter to execute the query");

        final Button commitButton = createToolButton("Commit", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    connection.commit();
                    Notification.show("Commited");
                } catch (SQLException ex) {
                    LOG.warn("commit failed", ex);
                    Notification.show("Commit failed", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        commitButton.setEnabled(false);
        final Button rollbackButton = createToolButton("Rollback", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    connection.rollback();
                    Notification.show("Rolled back");
                } catch (SQLException ex) {
                    LOG.warn("rollback failed", ex);
                    Notification.show("Rollback failed", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        rollbackButton.setEnabled(false);

        final CheckBox autocommitCheckBox = new CheckBox("Autocommit", DEFAULT_AUTO_COMMIT);
        autocommitCheckBox.setImmediate(true);
        autocommitCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean autoCommit = Boolean.TRUE.equals(autocommitCheckBox.getValue());
                try {
                    connection.setAutoCommit(autoCommit);
                    commitButton.setEnabled(!autoCommit);
                    rollbackButton.setEnabled(!autoCommit);
                } catch (Exception ex) {
                    autocommitCheckBox.setValue(!autoCommit);
                    Notification.show("Failed to change autocommit setting", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        maxRowsBox = new ComboBox(null, Arrays.asList(10, 100, 500, 1000));
        maxRowsBox.setDescription("Max number of rows to retrieve");
        maxRowsBox.setWidth("100px");
        maxRowsBox.setNewItemsAllowed(false);
        maxRowsBox.setNullSelectionAllowed(false);
        maxRowsBox.setTextInputAllowed(false);
        maxRowsBox.setValue(100);

        queryOptionsLayout = new HorizontalLayout(
                UiUtils.createHorizontalSpacer(5),
                executeButton,
                UiUtils.createHorizontalSpacer(5),
                commitButton,
                rollbackButton,
                UiUtils.createHorizontalSpacer(5),
                autocommitCheckBox,
                maxRowsBox);
        queryOptionsLayout.setWidth("100%");
        queryOptionsLayout.setExpandRatio(autocommitCheckBox, 1);
        queryOptionsLayout.setComponentAlignment(executeButton, Alignment.MIDDLE_LEFT);
        queryOptionsLayout.setComponentAlignment(commitButton, Alignment.MIDDLE_LEFT);
        queryOptionsLayout.setComponentAlignment(rollbackButton, Alignment.MIDDLE_LEFT);
        queryOptionsLayout.setComponentAlignment(autocommitCheckBox, Alignment.MIDDLE_LEFT);
        queryOptionsLayout.setComponentAlignment(maxRowsBox, Alignment.MIDDLE_RIGHT);
    }

    private void buildQueryTextEditor() {
        query = new TextArea();
        query.setSizeFull();
        query.setStyleName("monospace");
        query.addShortcutListener(new ShortcutListener("Run Query", null, ShortcutAction.KeyCode.ENTER, ShortcutAction.ModifierKey.CTRL) {
            @Override
            public void handleAction(Object sender, Object target) {
                executeQuery(query.getValue());
            }
        });
    }

    protected Button createToolButton(String caption, Button.ClickListener clickListener) {
        final Button button = new Button(caption, clickListener);
        button.addStyleName(Reindeer.BUTTON_SMALL);
        return button;
    }

    protected void executeQuery(final String sql) {
        if (sql == null || sql.isEmpty()) {
            return;
        }
        // TODO cancelable execution
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(progressBar);
        vl.setComponentAlignment(progressBar, Alignment.MIDDLE_CENTER);
        splitPanel.setSecondComponent(vl);
        setExecutionAllowed(false);
        EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                handleQueryExecution(sql);
            }
        });
    }

    private void handleQueryExecution(String sql) {
        final long start = System.currentTimeMillis();
        String statMsg = "";
        try {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setMaxRows(getMaxRows());
                boolean hasResultSet = stmt.execute();
                if (hasResultSet) {
                    ResultSetTable table = new ResultSetTable(stmt.getResultSet());
                    statMsg = "rows fetched: " + table.getItemIds().size();
                    showResult(table);
                } else {
                    int cnt = stmt.getUpdateCount();
                    statMsg = "rows updated: " + cnt;
                    showResult(new Label("Updated " + cnt + " row(s)"));
                }
            }

            final long end = System.currentTimeMillis();
            final String finalStatMsg = statMsg;
            getUI().access(new Runnable() {
                @Override
                public void run() {
                    Notification.show(
                            "Query Stats",
                            "exec time: " + (end - start) / 1000.0 + " ms\n" + finalStatMsg,
                            Notification.Type.TRAY_NOTIFICATION);
                }
            });
        } catch (SQLException e) {
            LOG.debug("failed to execute sql query", e);
            showResult(new Label(e.getMessage()));
        }
    }

    private void showResult(final Component resultComponent) {
        getUI().access(new Runnable() {
            @Override
            public void run() {
                splitPanel.setSecondComponent(resultComponent);
                query.focus();
                setExecutionAllowed(true);
            }
        });
    }

    private int getMaxRows() {
        Object value = maxRowsBox.getValue();
        return value == null ? 0 : (Integer) value;
    }

    private void setExecutionAllowed(boolean b) {
        query.setReadOnly(!b);
        queryOptionsLayout.setEnabled(b);
    }
}
