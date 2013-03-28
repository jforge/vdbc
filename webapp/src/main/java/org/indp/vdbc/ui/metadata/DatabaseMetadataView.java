package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.workspace.ModuleContentComponent;
import org.indp.vdbc.util.JdbcUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 *
 */
public class DatabaseMetadataView extends ModuleContentComponent {
    private final DatabaseSession databaseSession;
    private Connection connection;

    public interface BrowserCallback {

        void showDetails(Component detailsComponent);
    }

    public DatabaseMetadataView(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
    }

    @Override
    protected Component createContent() throws Exception {
        connection = databaseSession.getConnection();

        final HorizontalSplitPanel sp = new HorizontalSplitPanel();

        BrowserCallback bc = new BrowserCallback() {

            @Override
            public void showDetails(Component detailsComponent) {
                detailsComponent.setSizeFull();
                sp.setSecondComponent(detailsComponent);
            }
        };

        sp.setSizeFull();
        sp.setFirstComponent(createSectionLinks(bc));
        sp.setSecondComponent(new Label());
        sp.setSplitPosition(200, Unit.PIXELS);
        sp.setStyleName(Reindeer.TABSHEET_SMALL);
        return sp;
    }

    @Override
    protected String getTitle() {
        return "Database Metadata";
    }

    @Override
    protected void close() {
        JdbcUtils.close(connection);
    }

    protected Component createSectionLinks(BrowserCallback bc) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(true);
        vl.addComponents(
                new MetadataInfoSection("Catalogs", bc, new ResultSetDetailsProvider() {

                    @Override
                    protected ResultSet getResultSet() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getCatalogs();
                    }
                }),
                new MetadataInfoSection("Schemas", bc, new ResultSetDetailsProvider() {

                    @Override
                    protected ResultSet getResultSet() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getSchemas();
                    }
                }),
                new MetadataInfoSection("Table types", bc, new ResultSetDetailsProvider() {

                    @Override
                    protected ResultSet getResultSet() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getTableTypes();
                    }
                }),
                new MetadataInfoSection("Tables", bc, new ResultSetDetailsProvider() {

                    @Override
                    protected ResultSet getResultSet() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getTables(null, null, null, null);
                    }
                }),
//        new MetadataInfoSection("Functions", bc, new ResultSetDetailsProvider() {
//
//            @Override
//            protected ResultSet getResultSet() throws Exception {
//                DatabaseMetaData md = connection.getMetaData();
//                return md.getFunctions(null, null, null);
//            }
//        }),
                new MetadataInfoSection("Procedures", bc, new ResultSetDetailsProvider() {

                    @Override
                    protected ResultSet getResultSet() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getProcedures(null, null, null);
                    }
                }),
                new MetadataInfoSection("System Functions", bc, new SeparatedValuesStringDetailsProvider() {

                    @Override
                    protected String getValueString() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getSystemFunctions();
                    }
                }),
                new MetadataInfoSection("String Functions", bc, new SeparatedValuesStringDetailsProvider() {

                    @Override
                    protected String getValueString() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getStringFunctions();
                    }
                }),
                new MetadataInfoSection("Numeric Functions", bc, new SeparatedValuesStringDetailsProvider() {

                    @Override
                    protected String getValueString() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getNumericFunctions();
                    }
                }),
                new MetadataInfoSection("Time/Date Functions", bc, new SeparatedValuesStringDetailsProvider() {

                    @Override
                    protected String getValueString() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getTimeDateFunctions();
                    }
                }),
                new MetadataInfoSection("SQL Keywords", bc, new SeparatedValuesStringDetailsProvider() {

                    @Override
                    protected String getValueString() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getSQLKeywords();
                    }
                }),
                new MetadataInfoSection("Data Types", bc, new ResultSetDetailsProvider() {

                    @Override
                    protected ResultSet getResultSet() throws Exception {
                        DatabaseMetaData md = connection.getMetaData();
                        return md.getTypeInfo();
                    }
                }));

        return vl;
    }
}
