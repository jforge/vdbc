package org.indp.vdbc.ui.query;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.ResultSetTable;
import org.indp.vdbc.ui.UiUtils;
import org.indp.vdbc.ui.workspace.WorkspacePageComponent;
import org.indp.vdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

/**
 *
 *
 */
public class QueryExecutorComponent extends WorkspacePageComponent {

    private static final boolean DEFAULT_AUTO_COMMIT = true;
    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorComponent.class);

    private final DatabaseSession databaseSession;
    private Connection connection;
    //
    private VerticalSplitPanel splitPanel;
    private HorizontalLayout queryOptionsLayout;
    private TextArea query;
    private ComboBox maxRowsBox;

    public QueryExecutorComponent(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
        setCaption("Query");
    }

    @Override
    protected Component createContent() throws Exception {
        connection = databaseSession.getConnection();
        connection.setAutoCommit(DEFAULT_AUTO_COMMIT);

        buildQueryTextEditor();
        buildToolbar();

        splitPanel = new VerticalSplitPanel(query, new Label());
        splitPanel.setSizeFull();

        VerticalLayout vl = new VerticalLayout(queryOptionsLayout, splitPanel);
        vl.setSizeFull();
        vl.setExpandRatio(splitPanel, 1);
        return vl;
    }

    @Override
    protected void close() {
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
                String sql = query.getValue();
                if (sql != null && !sql.isEmpty()) {
                    executeQuery(sql);
                }
            }
        });
    }

    protected Button createToolButton(String caption, Button.ClickListener clickListener) {
        final Button button = new Button(caption, clickListener);
        button.addStyleName(Reindeer.BUTTON_SMALL);
        return button;
    }

    protected void executeQuery(final String sql) {
        final long start = System.currentTimeMillis();

        // TODO cancelable execution

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setIndeterminate(true);
        progressIndicator.setPollingInterval(500);
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(progressIndicator);
        vl.setComponentAlignment(progressIndicator, Alignment.MIDDLE_CENTER);
        splitPanel.setSecondComponent(vl);
        setExecutionAllowed(false);

        final UI currentUI = UI.getCurrent();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String statMsg = "";
                Component resultComponent;
                try {
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    try {
                        stmt.setMaxRows(getMaxRows());
                        boolean hasResultSet = stmt.execute();
                        if (hasResultSet) {
                            ResultSetTable table = new ResultSetTable(stmt.getResultSet());
                            statMsg = "rows fetched: " + table.getItemIds().size();
                            resultComponent = table;
                        } else {
                            int cnt = stmt.getUpdateCount();
                            statMsg = "rows updated: " + cnt;
                            resultComponent = new Label("Updated " + cnt + " row(s)");
                        }
                    } finally {
                        JdbcUtils.close(stmt);
                    }

                    long end = System.currentTimeMillis();

                    currentUI.getSession().lock();
                    try {
                        Notification.show(
                                "Query Stats",
                                "exec time: " + (end - start) / 1000.0 + " ms\n" + statMsg,
                                Notification.Type.TRAY_NOTIFICATION);
                    } finally {
                        currentUI.getSession().unlock();
                    }
                } catch (SQLException e) {
                    LOG.debug("failed to execute sql query", e);
                    resultComponent = new Label(e.getMessage());
                }

                currentUI.getSession().lock();
                try {
                    splitPanel.setSecondComponent(resultComponent);
                    query.focus();
                    setExecutionAllowed(true);
                } finally {
                    currentUI.getSession().unlock();
                }
            }
        }, "vdbc-query-" + System.currentTimeMillis());
        thread.start();
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
