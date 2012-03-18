package org.indp.vdbc.util;

import org.indp.vdbc.model.jdbc.JdbcTable;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MetadataRetriever implements Closeable {

    private final DatabaseMetaData metaData;
    private final Connection connection;

    public MetadataRetriever(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
        this.metaData = connection.getMetaData();
    }

    @Override
    public void close() throws IOException {
        JdbcUtils.close(connection);
    }

    public DatabaseMetaData getRawMetadata() {
        return metaData;
    }

    public List<String> getCatalogs() throws SQLException {
        List<String> rez = new ArrayList<String>();
        ResultSet catalogs = metaData.getCatalogs();
        while (catalogs.next())
            rez.add(catalogs.getString("TABLE_CAT"));
        return rez;
    }

    public String getCatalogTerm() throws SQLException {
        String term = metaData.getCatalogTerm();
        return null == term || term.isEmpty() ? "catalog" : term;
    }

    public List<String> getSchemas() throws SQLException {
        List<String> rez = new ArrayList<String>();
        ResultSet schemas = metaData.getSchemas();
        while (schemas.next())
            rez.add(schemas.getString("TABLE_SCHEM"));
        schemas.close();
        return rez;
    }

    public String getSchemaTerm() throws SQLException {
        String term = metaData.getSchemaTerm();
        return null == term || term.isEmpty() ? "schema" : term;
    }

    public List<String> getTableTypes() throws SQLException {
        List<String> rez = new ArrayList<String>();
        ResultSet types = metaData.getTableTypes();
        while (types.next())
            rez.add(types.getString("TABLE_TYPE"));
        types.close();
        return rez;
    }

    public List<JdbcTable> getTables(String catalog, String schema, String tableType) throws SQLException {
        ArrayList<JdbcTable> rez = new ArrayList<JdbcTable>();
        ResultSet tables = metaData.getTables(catalog, schema, null, new String[]{tableType});
        while (tables.next())
            rez.add(new JdbcTable(
                    tables.getString("TABLE_CAT"),
                    tables.getString("TABLE_SCHEM"),
                    tables.getString("TABLE_NAME")));
        tables.close();
        return rez;
    }
}
