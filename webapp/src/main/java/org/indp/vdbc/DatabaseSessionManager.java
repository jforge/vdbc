package org.indp.vdbc;

import com.google.common.base.Strings;
import org.apache.commons.dbcp.BasicDataSource;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * session scoped
 */
public class DatabaseSessionManager implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSessionManager.class);
    private ConnectionProfile connectionProfile;
    private BasicDataSource dataSource;

    //    @PreDestroy
    public void destroy() {
        disconnect();
    }

    public void test(String driver, String url, String user, String password) throws Exception {
        Connection conn = JdbcUtils.getConnection(driver, url, user, password);
        JdbcUtils.close(conn);
    }

    public void connect(ConnectionProfile profile) throws Exception {
        test(profile.getDriver(), profile.getUrl(), profile.getUser(), profile.getPassword());
        disconnect();

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(profile.getDriver());
        dataSource.setUrl(profile.getUrl());
        dataSource.setUsername(profile.getUser());
        dataSource.setPassword(profile.getPassword());

        if (!Strings.isNullOrEmpty(profile.getValidationQuery())) {
            dataSource.setValidationQuery(profile.getValidationQuery());
        }

        dataSource.setMaxActive(32);
        dataSource.setMaxIdle(4);
        dataSource.setMaxWait(20 * 1000);
        dataSource.setMaxOpenPreparedStatements(8);

        connectionProfile = profile;
    }

    public void disconnect() {
        LOG.info("cleaning up...");
        connectionProfile = null;
        if (null != dataSource)
            try {
                dataSource.close();
                dataSource = null;
            } catch (Exception ex) {
                LOG.warn("failed to close the data source", ex);
            }
    }

    public ConnectionProfile getConnectionProfile() {
        return connectionProfile;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
