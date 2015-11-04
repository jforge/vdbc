package org.indp.vdbc.db.impl;

import org.indp.vdbc.db.DialectFeatureNotSupportedException;
import org.indp.vdbc.db.Expressions;
import org.indp.vdbc.db.impl.model.DialectDefinition;
import org.indp.vdbc.db.impl.model.DialectFeature;
import org.mvel2.templates.TemplateRuntime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XmlExpressions implements Expressions {

    private final DialectDefinition dialectDefinition;

    public XmlExpressions(DialectDefinition dialectDefinition) {
        this.dialectDefinition = dialectDefinition;
    }

    @Override
    public String buildTableName(String catalog, String schema, String table) {
        return eval("build.tableName",
                    var("catalog", catalog),
                    var("schema", schema),
                    var("table", table));
    }

    @Override
    public String selectAllFromTable(String tableName, String filter) {
        return eval("select.all.from.table",
                var("tableName", tableName),
                var("filter", filter));
    }

    @Override
    public String selectAllFromTable(String tableName, String filter, int offset, int limit) {
        return eval("select.all.from.table.limit.offset",
                    var("tableName", tableName),
                    var("filter", filter),
                    var("offset", offset),
                    var("limit", limit));
    }

    @Override
    public String count(String tableName, String filter) {
        return eval("count.all",
                var("tableName", tableName),
                var("filter", filter));
    }

    @SuppressWarnings("unchecked")
	private <T> T eval(String featureId, ContextVar... vars) {
        Map<String, Object> map;
        if (vars == null) {
            map = Collections.emptyMap();
        } else {
            map = new HashMap<>(vars.length);
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
        return new ContextVar<>(name, value);
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
