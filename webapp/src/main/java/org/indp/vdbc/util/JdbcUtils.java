package org.indp.vdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pi
 */
public class JdbcUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcUtils.class);

    private JdbcUtils() {
    }

    public static Connection getConnection(String driver, String url, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        }
    }

    public static void close(Statement statement) {
        try {
            statement.close();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        }
    }

    public static void close(ResultSet rs) {
        try {
            rs.close();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        }
    }
}
