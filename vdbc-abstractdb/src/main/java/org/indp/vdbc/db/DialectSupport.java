package org.indp.vdbc.db;

import org.indp.vdbc.db.impl.DerbyDialect;
import org.indp.vdbc.db.impl.GenericDialect;
import org.indp.vdbc.db.impl.H2Dialect;
import org.indp.vdbc.db.impl.MysqlDialect;
import org.indp.vdbc.db.impl.OracleDialect;
import org.indp.vdbc.db.impl.PostgresqlDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class DialectSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DialectSupport.class);

    private static final Map<String, Dialect> DIALECTS = new LinkedHashMap<>();

    static {
        add(new GenericDialect());
        add(new H2Dialect());
        add(new DerbyDialect());
        add(new OracleDialect());
        add(new PostgresqlDialect());
        add(new MysqlDialect());
    }

    public static final String VDBCIGNORE_MARKER = "VDBCIGNORE";

    public static Dialect getDialect(String id) {
        return DIALECTS.containsKey(id)
                ? DIALECTS.get(id)
                : null;
    }

    public static Dialect getGenericDialect() {
        return getDialect("generic");
    }

    public static Collection<String> getDialectIds() {
        return DIALECTS.keySet();
    }

    private static void add(Dialect dialect) {
        DIALECTS.put(dialect.getId(), dialect);
    }

    public static boolean isServiceColumn(String name) {
        return name.contains(VDBCIGNORE_MARKER);
    }

    private DialectSupport() {
    }
}
