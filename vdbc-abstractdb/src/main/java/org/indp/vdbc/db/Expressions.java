package org.indp.vdbc.db;

public interface Expressions {

    String buildTableName(String catalog, String schema, String table);

    String selectAllFromTable(String tableName, String filter);

    String selectAllFromTable(String tableName, String filter, int offset, int limit);

    String count(String tableName, String filter);
}
