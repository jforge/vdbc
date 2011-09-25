package org.indp.vdbc.ui.explorer.details;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.Reindeer;
import java.sql.Connection;
import org.indp.vdbc.model.jdbc.JdbcTable;

/**
 *
 * @author pi
 */
public class TableDetailsView extends CustomComponent {

    private final TabSheet tabSheet;

    public TableDetailsView(JdbcTable table, Connection connection) {
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addStyleName(Reindeer.TABSHEET_SMALL);
        setCompositionRoot(tabSheet);

        TableStructureView tableStructureView = new TableStructureView(table, connection);
        tableStructureView.setSizeFull();

        TableDataView tableContentView = new TableDataView(table, connection);
        tableContentView.setSizeFull();

        tabSheet.addTab(tableStructureView);
        tabSheet.addTab(tableContentView);
    }
}
