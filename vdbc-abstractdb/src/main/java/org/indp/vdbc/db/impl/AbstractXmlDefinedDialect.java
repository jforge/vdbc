package org.indp.vdbc.db.impl;

import org.indp.vdbc.db.Dialect;
import org.indp.vdbc.db.Expressions;
import org.indp.vdbc.db.impl.model.DialectDefinition;

import java.util.List;

/**
 *
 */
public abstract class AbstractXmlDefinedDialect implements Dialect {

    private final DialectDefinition dialectDefinition;
    private final String id;

    private Expressions expressions;

    protected AbstractXmlDefinedDialect(String id) {
        this.id = id;
        this.dialectDefinition = DialectDefinition.read(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return dialectDefinition.getName();
    }

    @Override
    public List<String> getExampleUrls() {
        return dialectDefinition.getExampleUrls();
    }

    @Override
    public boolean supportsLimitedSelects() {
        return dialectDefinition.hasFeature("select.all.from.table.limit.offset");
    }

    @Override
    public Expressions getExpressions() {
        if (expressions == null) {
            expressions = new XmlExpressions(dialectDefinition);
        }
        return expressions;
    }
}
