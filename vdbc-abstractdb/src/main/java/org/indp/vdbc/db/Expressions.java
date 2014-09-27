package org.indp.vdbc.db;

public interface Expressions {

    String buildTableName(String catalog, String schema, String table);

    String selectAllFromTable(String tableName);

    String selectAllFromTable(String tableName, int offset, int limit);

    String count(String tableName);
}
