package org.indp.vdbc.db.impl;

import org.indp.vdbc.db.DialectFeatureNotSupportedException;
import org.indp.vdbc.db.Expressions;
import org.indp.vdbc.db.impl.model.DialectDefinition;
import org.indp.vdbc.db.impl.model.DialectFeature;
import org.mvel2.templates.TemplateRuntime;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class XmlExpressions implements Expressions {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(DialectDefinition.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXB context.");
        }
    }

    private final DialectDefinition dialectDefinition;

    public XmlExpressions(String id) {
        InputStream in;
        try {
            in = XmlExpressions.class.getResource(id + ".xml").openStream();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read xml expressions for dialect " + id, e);
        }
        try {
            dialectDefinition = (DialectDefinition) JAXB_CONTEXT.createUnmarshaller().unmarshal(in);
            if (dialectDefinition.getBaseDialect() != null) {
                mergeFeaturesFromBase(dialectDefinition);
            }
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to parse dialect definition.", e);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public String selectAllFromTable(String tableName) {
        return eval("select.all.from.table", var("tableName", tableName));
    }

    @Override
    public String selectAllFromTable(String tableName, int offset, int limit) {
        return eval("select.all.from.table.limit.offset",
                var("tableName", tableName),
                var("offset", offset),
                var("limit", limit));
    }

    @Override
    public String count(String tableName) {
        return eval("count.all", var("tableName", tableName));
    }

    private <T> T eval(String featureId, ContextVar... vars) {
        Map<String, Object> map;
        if (vars == null) {
            map = Collections.emptyMap();
        } else {
            map = new HashMap<String, Object>(vars.length);
            for (ContextVar var : vars) {
                map.put(var.name, var.value);
            }
        }

        DialectFeature dialectFeature = dialectDefinition.getFeatures().get(featureId);
        if (dialectFeature == null) {
            throw new DialectFeatureNotSupportedException(featureId);
        }

        String template = dialectFeature.getExpression();
        return (T) TemplateRuntime.eval(template, map);
    }

    private <T> ContextVar<T> var(String name, T value) {
        return new ContextVar<T>(name, value);
    }

    private void mergeFeaturesFromBase(DialectDefinition dialectDefinition) {
        String baseDialect = dialectDefinition.getBaseDialect();
        Map<String, DialectFeature> ourFeatures = dialectDefinition.getFeatures();
        Map<String, DialectFeature> baseFeatures = new XmlExpressions(baseDialect).dialectDefinition.getFeatures();
        for (String key : baseFeatures.keySet()) {
            if (!ourFeatures.containsKey(key)) {
                ourFeatures.put(key, baseFeatures.get(key));
            }
        }
    }

    private class ContextVar<T> {
        private String name;
        private T value;

        public ContextVar(String name, T value) {
            this.name = name;
            this.value = value;
        }
    }
}
