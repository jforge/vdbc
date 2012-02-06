package org.indp.vdbc.model.jdbc;

/**
 *
 *
 */
public class JdbcTable {

    private String catalog;
    private String schema;
    private String name;

    public JdbcTable() {
    }

    public JdbcTable(String catalog, String schema, String name) {
        this.catalog = catalog;
        this.schema = schema;
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
