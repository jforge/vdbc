package org.indp.vdbc.util.dbaware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class StatementSource {
    private static final Logger LOG = LoggerFactory.getLogger(StatementSource.class);
    private static final Map<String, String> DBMS_NAMES;
    private static final Properties PROPERTIES;
    private String dbId;

    static {
        // originated from SQL Workbench/J
        Map<String, String> dbmsNames = new HashMap<String, String>();
        dbmsNames.put("H2", "h2");
        dbmsNames.put("Oracle", "oracle");
        dbmsNames.put("HSQLDB", "hsql_database_engine");
        dbmsNames.put("PostgreSQL", "postgresql");
        dbmsNames.put("DB2 (LUW)", "db2");
        dbmsNames.put("DB2 Host", "db2h");
        dbmsNames.put("DB2 iSeries", "db2i");
        dbmsNames.put("MySQL", "mysql");
        dbmsNames.put("Firebird SQL", "firebird");
        dbmsNames.put("Informix", "informix_dynamic_server");
        dbmsNames.put("SQL Anywhere", "sql_anywhere");
        dbmsNames.put("Microsoft SQL Server", "microsoft_sql_server");
        dbmsNames.put("Apache Derby", "apache_derby");
        DBMS_NAMES = Collections.unmodifiableMap(dbmsNames);

        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(StatementSource.class.getResourceAsStream("/workbench/resource/default.properties"));
        } catch (IOException e) {
            LOG.error("Failed to read statements", e);
        }
    }

    public static StatementSource create(Connection connection) throws SQLException {
        String productName = connection.getMetaData().getDatabaseProductName();
        for (String key : DBMS_NAMES.keySet()) {
            if (productName.contains(key)) {
                return new StatementSource(DBMS_NAMES.get(key));
            }
        }
        throw new RuntimeException("Unsupported product: " + productName);
    }

    private StatementSource(String dbId) {
        this.dbId = dbId;
    }

    public String getCurrentSchemaSql() {
        return PROPERTIES.getProperty(String.format("workbench.db.%s.currentschema.query", dbId));
    }
}
