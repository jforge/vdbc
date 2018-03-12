package org.indp.vdbc.ui.explorer.details;

import com.vaadin.ui.CustomComponent;
import com.vaadin.v7.ui.Label;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.ResultSetTable;
import org.indp.vdbc.util.JdbcUtils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 *
 *
 */
public class TableStructureView extends CustomComponent {

    public TableStructureView(JdbcTable table, DatabaseSession databaseSession) {
        setCaption("Structure");
        try {
            DatabaseMetaData metadata = databaseSession.getMetadata().getRawMetadata();
            ResultSet attributes = metadata.getColumns(table.getCatalog(), table.getSchema(), table.getName(), null);
            ResultSetTable resultSetTable = new ResultSetTable(attributes, Arrays.asList("COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE", "DECIMAL_DIGITS", "IS_NULLABLE", "IS_AUTOINCREMENT", "COLUMN_DEF", "REMARKS"));
            resultSetTable.setSizeFull();
            setCompositionRoot(resultSetTable);
            JdbcUtils.close(attributes);
        } catch (Exception ex) {
            setCompositionRoot(new Label(ex.getMessage()));
        }
    }
}
