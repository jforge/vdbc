package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.util.JdbcUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 *
 *
 */
public class DatabaseMetadataView extends CustomComponent {

    private Connection connection;

    public interface BrowserCallback {

        void showDetails(Component detailsComponent);
    }

    public DatabaseMetadataView(DatabaseSession databaseSession) {
        try {
            connection = databaseSession.getConnection();
        } catch (Exception ex) {
            setCompositionRoot(new Label(ex.getMessage()));
            return;
        }

        final HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setSizeFull();

        BrowserCallback bc = new BrowserCallback() {

            @Override
            public void showDetails(Component detailsComponent) {
                detailsComponent.setSizeFull();
                sp.setSecondComponent(detailsComponent);
            }
        };

        sp.setFirstComponent(createSectionLinks(bc));
        sp.setSecondComponent(new Label());
        sp.setSplitPosition(200, Unit.PIXELS);
        sp.setStyleName(Reindeer.TABSHEET_SMALL);

        setSizeFull();
        setCaption("Database Metadata");
        setCompositionRoot(sp);
    }

    @Override
    public void detach() {
        JdbcUtils.close(connection);
        super.detach();
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
