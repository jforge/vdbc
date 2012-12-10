package org.indp.vdbc.db;

/**
 *
 */
public interface Expressions {

    String selectAllFromTable(String tableName);

    String selectAllFromTable(String tableName, int offset, int limit);

    String count(String tableName);
}
