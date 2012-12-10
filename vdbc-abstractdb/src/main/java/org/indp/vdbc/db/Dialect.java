package org.indp.vdbc.db;

/**
 *
 */
public interface Dialect {
    String getId();
    Expressions getExpressions();
}
