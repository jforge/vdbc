package org.indp.vdbc.ui.explorer.details;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.ui.ResultSetTable;
import org.indp.vdbc.ui.Toolbar;
import org.indp.vdbc.ui.UiUtils;
import org.indp.vdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * @author pi
 */
public class TableDataView extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(TableDataView.class);
    //
//    private JdbcTable table;
    //private Connection connection;
    //
    private VerticalLayout tableContainer;
    private Property maxRowCount;

    public TableDataView(final JdbcTable table, final Connection connection) {
//        this.table = table;
//        this.connection = connection;

        setCaption("Data");

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        setCompositionRoot(vl);

        ComboBox limitsCombo = new ComboBox(null, Arrays.asList(100L, 250L, 500L, 1000L));
        limitsCombo.setNullSelectionAllowed(false);
        limitsCombo.setNullSelectionItemId(100L);
        limitsCombo.setNewItemsAllowed(false);
        limitsCombo.setWidth("100px");
        this.maxRowCount = limitsCombo;

        Toolbar toolbar = new Toolbar();
        toolbar.setWidth("100%");
        toolbar.addComponent(new Label("Row count:"));
        toolbar.addComponent(UiUtils.createHorizontalSpacer(5));
        toolbar.addComponent(limitsCombo);
        toolbar.addComponent(UiUtils.createHorizontalSpacer(5));
        toolbar.addComponent(new Button("Refresh", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                refreshDataView(table, connection);
            }
        }));

        tableContainer = new VerticalLayout();
        tableContainer.setSizeFull();
        vl.addComponent(toolbar);
        vl.addComponent(tableContainer);
        vl.setExpandRatio(tableContainer, 1f);

        refreshDataView(table, connection);
    }

    protected void refreshDataView(JdbcTable table, Connection connection) {
        Statement stmt = null;
        ResultSet rs = null;
        Component component;
        try {
            // TODO extract from view layer
            stmt = connection.createStatement();
            stmt.setMaxRows(null != maxRowCount.getValue() ? ((Long) maxRowCount.getValue()).intValue() : 100);
            rs = stmt.executeQuery("select * from " + createTableName(table, connection));
            component = new ResultSetTable(rs);
            component.setSizeFull();
        } catch (SQLException ex) {
            LOG.warn("failed to retrieve table data", ex);
            component = new Label(ex.getMessage());
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        tableContainer.removeAllComponents();
        tableContainer.addComponent(component);
    }

    protected String createTableName(JdbcTable table, Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        StringBuilder sb = new StringBuilder();
        if (null != table.getCatalog() && !table.getCatalog().isEmpty() && metaData.supportsCatalogsInTableDefinitions())
            sb.append(table.getCatalog()).append(".");
        if (null != table.getSchema() && !table.getSchema().isEmpty() && metaData.supportsSchemasInTableDefinitions())
            sb.append(table.getSchema()).append(".");
        sb.append(table.getName());
        return sb.toString();
    }
}
