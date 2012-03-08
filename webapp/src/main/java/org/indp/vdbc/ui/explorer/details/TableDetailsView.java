package org.indp.vdbc.ui.explorer.details;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.DatabaseSessionManager;
import org.indp.vdbc.model.jdbc.JdbcTable;

/**
 *
 *
 */
public class TableDetailsView extends CustomComponent {

    public TableDetailsView(JdbcTable table, DatabaseSessionManager sessionManager) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addStyleName(Reindeer.TABSHEET_SMALL);
        setCompositionRoot(tabSheet);

        TableStructureView tableStructureView = new TableStructureView(table, sessionManager);
        tableStructureView.setSizeFull();

        TableDataView tableContentView = new TableDataView(table, sessionManager);
        tableContentView.setSizeFull();

        TableSourceView tableSourceView = new TableSourceView(table, sessionManager);
        tableSourceView.setSizeFull();

        tabSheet.addTab(tableStructureView);
        tabSheet.addTab(tableContentView);
        tabSheet.addTab(tableSourceView);
    }
}
