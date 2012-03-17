package org.indp.vdbc.ui.query;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.Notification;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.indp.vdbc.services.DatabaseSessionManager;
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
public class QueryExecutorView extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorView.class);
    private Connection connection;
    //
    private VerticalSplitPanel splitPanel;
    private QueryOptionsView queryOptionsView;

    public QueryExecutorView(DatabaseSessionManager manager) {
        setCaption("Query");
        setSizeFull();

        try {
            connection = manager.getConnection();
        } catch (Exception ex) {
            LOG.warn("connection failed", ex);
            addComponent(new Label(ex.getMessage()));
            return;
        }

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        setCompositionRoot(vl);

        splitPanel = new VerticalSplitPanel();
        splitPanel.setSizeFull();

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
                    getApplication().getMainWindow().showNotification("Commited");
                } catch (SQLException ex) {
                    LOG.warn("commit failed", ex);
                    getApplication().getMainWindow().showNotification("Commit failed<br/>", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        queryOptionsView.setRollbackActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connection.rollback();
                    getApplication().getMainWindow().showNotification("Rolled back");
                } catch (SQLException ex) {
                    LOG.warn("rollback failed", ex);
                    getApplication().getMainWindow().showNotification("Rollback failed<br/>", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        queryOptionsView.setAutocommitSettingListener(new QueryOptionsView.AutocommitSettingListener() {

            @Override
            public void setAutoCommit(boolean enabled) throws Exception {
                connection.setAutoCommit(enabled);
            }
        });
        queryOptionsView.setFormatSqlActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO format selected text
                String sql = query.getValue().toString();
                String formatted = new BasicFormatterImpl().format(sql);
                if (formatted.startsWith("\n")) {
                    formatted = formatted.substring(1);
                }
                query.setValue(formatted);
            }
        });

        splitPanel.setFirstComponent(query);
        splitPanel.setSecondComponent(new Label());

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

    protected void executeQuery(String query) {
        try {
            long start = System.currentTimeMillis();
            String statMsg = "";

            // TODO cancelable execution
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setMaxRows(queryOptionsView.getMaxRows());
            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                ResultSetTable table = new ResultSetTable(stmt.getResultSet());
                splitPanel.setSecondComponent(table);
                statMsg = "rows fetched: " + table.getItemIds().size();
            } else {
                int cnt = stmt.getUpdateCount();
                splitPanel.setSecondComponent(new Label("Updated " + cnt + " row(s)"));
                statMsg = "rows updated: " + cnt;
            }
            JdbcUtils.close(stmt);

            long end = System.currentTimeMillis();
            getApplication().getMainWindow().showNotification(
                    "Query Stats<br/>",
                    "exec time: " + (end - start) / 1000.0 + " ms<br/>" + statMsg,
                    Notification.TYPE_TRAY_NOTIFICATION);

        } catch (SQLException ex) {
            LOG.debug("failed to execute sql query", ex);
            splitPanel.setSecondComponent(new Label(ex.getMessage()));
        }
    }

    @Override
    public void detach() {
        JdbcUtils.close(connection);
        super.detach();
    }
}
