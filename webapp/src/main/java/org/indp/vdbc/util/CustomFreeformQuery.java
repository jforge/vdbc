package org.indp.vdbc.util;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * WARNING: this class strongly depends on FreeformQuery implementation details and may bring us surprise some day
 * TODO remove this class
 */
public class CustomFreeformQuery extends FreeformQuery {

    public CustomFreeformQuery(String queryString, JDBCConnectionPool connectionPool, String... primaryKeyColumns) {
        super(queryString, connectionPool, primaryKeyColumns);
    }

    @Override
    public ResultSet getResults(int offset, int pagelength) throws SQLException {
        Field field;
        try {
            field = FreeformQuery.class.getDeclaredField("activeConnection");
            field.setAccessible(true);
            Connection activeConnection = (Connection) field.get(this);
            StatementHelper sh = ((FreeformStatementDelegate) getDelegate()).getQueryStatement(offset, pagelength);
            PreparedStatement pstmt = activeConnection.prepareStatement(sh.getQueryString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sh.setParameterValuesToStatement(pstmt);
            return pstmt.executeQuery();
        } catch (NoSuchFieldException e) {
            throw new SQLException(e);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }
}
