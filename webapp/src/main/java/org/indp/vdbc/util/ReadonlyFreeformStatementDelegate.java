package org.indp.vdbc.util;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.sqlcontainer.RowItem;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.v7.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;
import org.indp.vdbc.db.Dialect;
import org.indp.vdbc.services.DatabaseSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReadonlyFreeformStatementDelegate implements FreeformStatementDelegate {
    private final String tableName;
    private final String filter;
    private final Dialect dialect;

    public ReadonlyFreeformStatementDelegate(String tableName, String filter, DatabaseSession databaseSession) {
        this.tableName = tableName;
        this.filter = filter;
        this.dialect = databaseSession.getDialect();
    }

    @Override
    public StatementHelper getQueryStatement(int offset, int limit) throws UnsupportedOperationException {
        StatementHelper helper = new StatementHelper();
        helper.setQueryString(dialect.getExpressions().selectAllFromTable(tableName, filter, offset, limit));
        return helper;
    }

    @Override
    public StatementHelper getCountStatement() throws UnsupportedOperationException {
        StatementHelper helper = new StatementHelper();
        helper.setQueryString(dialect.getExpressions().count(tableName, filter));
        return helper;
    }

    @Override
    public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getQueryString(int offset, int limit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCountQuery() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFilters(List<Container.Filter> filters) throws UnsupportedOperationException {
    }

    @Override
    public void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException {
    }

    @Override
    public int storeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContainsRowQueryString(Object... keys) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
