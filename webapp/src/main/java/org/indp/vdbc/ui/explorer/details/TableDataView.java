package org.indp.vdbc.ui.explorer.details;

import com.google.common.base.Strings;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.J2EEConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.event.Action;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 *
 */
public class TableDataView extends CustomComponent implements ToolbarOwner {

    private static final Logger LOG = LoggerFactory.getLogger(TableDataView.class);
    private final VerticalLayout tableContainer;
    private final J2EEConnectionPool connectionPool;
    private final HorizontalLayout toolbar;

    public TableDataView(final JdbcTable table, final DatabaseSessionManager sessionManager) {
        connectionPool = new J2EEConnectionPool(sessionManager.getDataSource());

        setCaption("Data");

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        setCompositionRoot(vl);

        toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        Button refreshButton = new Button("Refresh", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                refreshDataView(table, sessionManager);
            }

        });
        refreshButton.setStyleName(Reindeer.BUTTON_SMALL);
        toolbar.addComponent(refreshButton);
        toolbar.setComponentAlignment(refreshButton, Alignment.MIDDLE_RIGHT);


        tableContainer = new VerticalLayout();
        tableContainer.setSizeFull();

        vl.addComponent(tableContainer);
        vl.setExpandRatio(tableContainer, 1f);

        refreshDataView(table, sessionManager);
    }

    protected void refreshDataView(JdbcTable tableDefinition, DatabaseSessionManager sessionManager) {
        Component component;
        try {
            FreeformQuery query = new FreeformQuery("select * from " + createTableName(tableDefinition, sessionManager), connectionPool);
            SQLContainer container = new SQLContainer(query);
            Table table = new Table(null, container);
            table.setSelectable(true);
            table.setSortDisabled(true);
            table.setColumnReorderingAllowed(true);
            table.setColumnCollapsingAllowed(true);
            table.setSizeFull();
            table.addActionHandler(new Action.Handler() {
                private final String singleRecordViewAction = "Single record view...";

                @Override
                public Action[] getActions(Object target, Object sender) {
                    return new Action[] {new Action(singleRecordViewAction)};
                }

                @Override
                public void handleAction(Action action, Object sender, Object target) {
                    if (!singleRecordViewAction.equals(action.getCaption())) {
                        return;
                    }
                    getApplication().getMainWindow().addWindow(new SingleRecordViewWindow());
                }
            });
            component = table;
        } catch (SQLException e) {
            LOG.warn("failed to retrieve tableDefinition data", e);
            component = new Label(e.getMessage());
        }
        tableContainer.removeAllComponents();
        tableContainer.addComponent(component);
    }

    protected String createTableName(JdbcTable table, DatabaseSessionManager sessionManager) throws SQLException {
        DatabaseMetaData metaData = sessionManager.getMetadata().getRawMetadata();
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
