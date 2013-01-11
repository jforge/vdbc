package org.indp.vdbc.ui.query;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.ResultSetTable;
import org.indp.vdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 *
 */
public class QueryExecutorComponent extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorComponent.class);
    private Connection connection;
    //
    private VerticalSplitPanel splitPanel;
    private QueryOptionsView queryOptionsView;

    public QueryExecutorComponent(DatabaseSession databaseSession) {
        setCaption("Query");
        setSizeFull();

        try {
            connection = databaseSession.getConnection();
        } catch (Exception ex) {
            LOG.warn("connection failed", ex);
            setCompositionRoot(new Label(ex.getMessage()));
            return;
        }

        final TextField query = new TextField();
        query.setSizeFull();
        query.addStyleName("monospace");

        queryOptionsView = new QueryOptionsView();
        queryOptionsView.setExecuteActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                executeQuery(query.getValue().toString());
            }
        });
        queryOptionsView.setCommitActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connection.commit();
                    Notification.show("Commited");
                } catch (SQLException ex) {
                    LOG.warn("commit failed", ex);
                    Notification.show("Commit failed<br/>", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        queryOptionsView.setRollbackActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connection.rollback();
                    Notification.show("Rolled back");
                } catch (SQLException ex) {
                    LOG.warn("rollback failed", ex);
                    Notification.show("Rollback failed<br/>", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        queryOptionsView.setAutocommitSettingListener(new QueryOptionsView.AutocommitSettingListener() {

            @Override
            public void setAutoCommit(boolean enabled) throws Exception {
                connection.setAutoCommit(enabled);
            }
        });
//        queryOptionsView.setFormatSqlActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // TODO format selected text
//                String sql = query.getValue().toString();
//                String formatted = new BasicFormatterImpl().format(sql);
//                if (formatted.startsWith("\n")) {
//                    formatted = formatted.substring(1);
//                }
//                query.setValue(formatted);
//            }
//        });

        splitPanel = new VerticalSplitPanel();
        splitPanel.setSizeFull();
        splitPanel.setFirstComponent(query);
        splitPanel.setSecondComponent(new Label());

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        setCompositionRoot(vl);

        vl.addComponent(queryOptionsView);
        vl.addComponent(splitPanel);
        vl.setExpandRatio(splitPanel, 1);

        query.addShortcutListener(new ShortcutListener("Run Query", null, ShortcutAction.KeyCode.ENTER, ShortcutAction.ModifierKey.CTRL) {

            @Override
            public void handleAction(Object sender, Object target) {
                executeQuery(query.getValue().toString());
            }
        });
    }

    protected void executeQuery(final String query) {
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

        final UI currentUI = UI.getCurrent();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String statMsg = "";
                Component resultComponent;
                try {
                    PreparedStatement stmt = connection.prepareStatement(query);
                    try {
                        stmt.setMaxRows(queryOptionsView.getMaxRows());
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
                    synchronized (currentUI) {
                        Notification.show(
                                "Query Stats<br/>",
                                "exec time: " + (end - start) / 1000.0 + " ms<br/>" + statMsg,
                                Notification.Type.TRAY_NOTIFICATION);
                    }
                } catch (SQLException e) {
                    LOG.debug("failed to execute sql query", e);
                    resultComponent = new Label(e.getMessage());
                }
                synchronized (currentUI) {
                    splitPanel.setSecondComponent(resultComponent);
                }
            }
        }, "vdbc-query-" + System.currentTimeMillis());
        thread.start();
    }

    @Override
    public void detach() {
        JdbcUtils.close(connection);
        super.detach();
    }
}
