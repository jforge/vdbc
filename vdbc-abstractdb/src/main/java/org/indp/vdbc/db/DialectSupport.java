package org.indp.vdbc.db;

import org.indp.vdbc.db.impl.DerbyDialect;
import org.indp.vdbc.db.impl.GenericDialect;
import org.indp.vdbc.db.impl.H2Dialect;
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

    private static final Map<String, Dialect> DIALECTS = new LinkedHashMap<String, Dialect>();

    static {
        add(new GenericDialect());
        add(new H2Dialect());
        add(new DerbyDialect());
    }

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

    private DialectSupport() {
    }

    public static class DialectItem {
        private String id;
        private String title;
        private Class<? extends Dialect> dialectClass;

        private DialectItem(String id, String title, Class<? extends Dialect> dialectClass) {
            this.id = id;
            this.title = title;
            this.dialectClass = dialectClass;
        }

        public String getTitle() {
            return title;
        }
    }
}
