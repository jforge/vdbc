package org.indp.vdbc.util;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import org.hibernate.dialect.Dialect;
import org.indp.vdbc.services.DatabaseSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class ReadonlyFreeformStatementDelegate implements FreeformStatementDelegate {
    private final String tableName;
    private final Dialect dialect;

    public ReadonlyFreeformStatementDelegate(String tableName, DatabaseSession databaseSession) {
        this.tableName = tableName;
        this.dialect = databaseSession.getDialect();
    }

    @Override
    public StatementHelper getQueryStatement(int offset, int limit) throws UnsupportedOperationException {
        StatementHelper helper = new StatementHelper();
        helper.setQueryString(dialect.getLimitString("select * from " + tableName, offset, limit));
        if (dialect.supportsVariableLimit()) {
            if (dialect.bindLimitParametersInReverseOrder()) {
                helper.addParameterValue(limit);
                if (offset > 0) {
                    helper.addParameterValue(offset);
                }
            } else {
                if (offset > 0) {
                    helper.addParameterValue(offset);
                }
                helper.addParameterValue(limit);
            }
        }
        return helper;
    }

    @Override
    public StatementHelper getCountStatement() throws UnsupportedOperationException {
        StatementHelper helper = new StatementHelper();
        helper.setQueryString("select count(*) from " + tableName);
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
