package org.indp.vdbc.ui.explorer.details;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.ui.ResultSetTable;
import org.indp.vdbc.util.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 *
 *
 */
public class TableStructureView extends CustomComponent {

    public TableStructureView(JdbcTable table, Connection connection) {
        setCaption("Structure");
        try {
            ResultSet attributes = connection.getMetaData().getColumns(table.getCatalog(), table.getSchema(), table.getName(), null);
            ResultSetTable resultSetTable = new ResultSetTable(attributes, Arrays.asList("COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE", "DECIMAL_DIGITS", "IS_NULLABLE", "IS_AUTOINCREMENT", "COLUMN_DEF", "REMARKS"));
            resultSetTable.setSizeFull();
            setCompositionRoot(resultSetTable);
            JdbcUtils.close(attributes);
        } catch (Exception ex) {
            setCompositionRoot(new Label(ex.getMessage()));
        }
    }
}
