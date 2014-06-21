package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.util.JdbcUtils;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMetadataView extends HorizontalSplitPanel implements Closeable {
    private final Connection connection;

    public interface BrowserCallback {

        void showDetails(Component detailsComponent);
    }

    public DatabaseMetadataView(DatabaseSession databaseSession) throws SQLException {
        connection = databaseSession.getConnection();

        BrowserCallback bc = new BrowserCallback() {

            @Override
            public void showDetails(Component detailsComponent) {
                detailsComponent.setSizeFull();
                setSecondComponent(detailsComponent);
            }
        };

        setSizeFull();
        setFirstComponent(createSectionLinks(bc));
        setSecondComponent(new Label());
        setSplitPosition(200, Unit.PIXELS);
        setStyleName(Reindeer.TABSHEET_SMALL);
        setCaption("Database Overview");
    }


    @Override
    public void close() {
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
