package org.indp.vdbc.services;

import org.hibernate.dialect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DialectSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DialectSupport.class);

    private static final Map<String, DialectItem> DIALECTS = new HashMap<String, DialectItem>();

    static {
        add(new DialectItem("h2", "H2", H2Dialect.class));
        add(new DialectItem("derby107", "Derby 10.7 and later", DerbyTenSevenDialect.class));
        add(new DialectItem("mysql5", "MySql 5", MySQL5Dialect.class));
        add(new DialectItem("mysql5innodb", "MySql 5 InnoDB", MySQL5InnoDBDialect.class));
        add(new DialectItem("oracle10g", "Oracle 10g", Oracle10gDialect.class));
    }

    public static Dialect getDialect(String id) {
        try {
            return DIALECTS.containsKey(id)
                    ? DIALECTS.get(id).dialectClass.newInstance()
                    : null;
        } catch (InstantiationException e) {
            LOG.error(e.getMessage(), e);
            return null;
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static Collection<DialectItem> getDialects() {
        return DIALECTS.values();
    }

    private static void add(DialectItem item) {
        DIALECTS.put(item.id, item);
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
    }
}
