package org.indp.vdbc.ui.explorer.details;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSessionManager;

/**
 *
 */
public class TableSourceView extends CustomComponent {

    public TableSourceView(JdbcTable table, DatabaseSessionManager sessionManager) {
        setCaption("Source");

//        try {
//            TableSourceBuilder builder = TableSourceBuilderFactory.getBuilder(ConnectionAdapter.create(sessionFactory));
//            String source = builder.getNativeTableSource(new TableIdentifier(table.getCatalog(), table.getSchema(), table.getName()), true);
//            setCompositionRoot(new Label(source));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            setCompositionRoot(new Label(e.getMessage()));
//        }

        setCompositionRoot(new Label("no info"));
    }
}
