package org.indp.vdbc.db;

import java.util.List;

/**
 *
 */
public interface Dialect {

    String getId();

    String getName();

    Expressions getExpressions();

    List<String> getExampleUrls();

    boolean supportsLimitedSelects();
}
