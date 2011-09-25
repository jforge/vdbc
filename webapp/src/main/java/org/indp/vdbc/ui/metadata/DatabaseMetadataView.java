package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import org.indp.vdbc.DatabaseSessionManager;
import org.indp.vdbc.util.JdbcUtils;

/**
 *
 * @author pi
 */
public class DatabaseMetadataView extends CustomComponent {

    private Connection connection;

    public interface BrowserCallback {

        void showDetails(Component detailsComponent);
    }

    public DatabaseMetadataView(DatabaseSessionManager sessionManager) {
        setCaption("Database Metadata");
        setSizeFull();

        try {
            connection = sessionManager.getConnection();
        } catch (Exception ex) {
            setCompositionRoot(new Label(ex.getMessage()));
        }

        final SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.setSizeFull();
        setCompositionRoot(sp);

        BrowserCallback bc = new BrowserCallback() {

            @Override
            public void showDetails(Component detailsComponent) {
                detailsComponent.setSizeFull();
                sp.setSecondComponent(detailsComponent);
            }
        };

        sp.setFirstComponent(createSectionLinks(bc));
        sp.setSecondComponent(new Label());
        sp.setSplitPosition(200, UNITS_PIXELS);
        sp.setStyleName("small");
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
        vl.addComponent(new MetadataInfoSection("Catalogs", bc, new ResultSetDetailsProvider() {

            @Override
            protected ResultSet getResultSet() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getCatalogs();
            }
        }));
        vl.addComponent(new MetadataInfoSection("Schemas", bc, new ResultSetDetailsProvider() {

            @Override
            protected ResultSet getResultSet() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getSchemas();
            }
        }));
        vl.addComponent(new MetadataInfoSection("Table types", bc, new ResultSetDetailsProvider() {

            @Override
            protected ResultSet getResultSet() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getTableTypes();
            }
        }));
        vl.addComponent(new MetadataInfoSection("Tables", bc, new ResultSetDetailsProvider() {

            @Override
            protected ResultSet getResultSet() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getTables(null, null, null, null);
            }
        }));
//        vl.addComponent(new MetadataInfoSection("Functions", bc, new ResultSetDetailsProvider() {
//
//            @Override
//            protected ResultSet getResultSet() throws Exception {
//                DatabaseMetaData md = connection.getMetaData();
//                return md.getFunctions(null, null, null);
//            }
//        }));
        vl.addComponent(new MetadataInfoSection("Procedures", bc, new ResultSetDetailsProvider() {

            @Override
            protected ResultSet getResultSet() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getProcedures(null, null, null);
            }
        }));
        vl.addComponent(new MetadataInfoSection("System Functions", bc, new SeparatedValuesStringDetailsProvider() {

            @Override
            protected String getValueString() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getSystemFunctions();
            }
        }));
        vl.addComponent(new MetadataInfoSection("String Functions", bc, new SeparatedValuesStringDetailsProvider() {

            @Override
            protected String getValueString() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getStringFunctions();
            }
        }));
        vl.addComponent(new MetadataInfoSection("Numeric Functions", bc, new SeparatedValuesStringDetailsProvider() {

            @Override
            protected String getValueString() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getNumericFunctions();
            }
        }));
        vl.addComponent(new MetadataInfoSection("Time/Date Functions", bc, new SeparatedValuesStringDetailsProvider() {

            @Override
            protected String getValueString() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getTimeDateFunctions();
            }
        }));
        vl.addComponent(new MetadataInfoSection("SQL Keywords", bc, new SeparatedValuesStringDetailsProvider() {

            @Override
            protected String getValueString() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getSQLKeywords();
            }
        }));
        vl.addComponent(new MetadataInfoSection("Data Types", bc, new ResultSetDetailsProvider() {

            @Override
            protected ResultSet getResultSet() throws Exception {
                DatabaseMetaData md = connection.getMetaData();
                return md.getTypeInfo();
            }
        }));

        return vl;
    }
}
