package org.indp.vdbc.util;

import com.vaadin.v7.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class CustomFreeformQuery extends FreeformQuery {

    public CustomFreeformQuery(String queryString, JDBCConnectionPool connectionPool, String... primaryKeyColumns) {
        super(queryString, connectionPool, primaryKeyColumns);
    }

    @Override
    public ResultSet getResults(int offset, int pagelength) throws SQLException {
        Connection connection = getConnection();
        StatementHelper sh = ((FreeformStatementDelegate) getDelegate()).getQueryStatement(offset, pagelength);
        PreparedStatement pstmt = connection.prepareStatement(sh.getQueryString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        sh.setParameterValuesToStatement(pstmt);
        return pstmt.executeQuery();
    }
}
