package org.indp.vdbc.db.impl;

import org.indp.vdbc.db.Dialect;
import org.indp.vdbc.db.Expressions;

/**
 *
 */
public abstract class AbstractDialect implements Dialect {
    private Expressions expressions;

    @Override
    public Expressions getExpressions() {
        if (expressions == null) {
            expressions = new XmlExpressions(getId());
        }
        return expressions;
    }
}
