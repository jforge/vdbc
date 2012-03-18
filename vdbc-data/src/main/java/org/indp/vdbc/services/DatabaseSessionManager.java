package org.indp.vdbc.services;

import org.indp.vdbc.exceptions.InvalidProfileException;
import org.indp.vdbc.model.DataSourceAdapter;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.util.JdbcUtils;
import org.indp.vdbc.util.MetadataRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * session scoped
 */
public class DatabaseSessionManager implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSessionManager.class);
    private ConnectionProfile connectionProfile;
    private DataSourceAdapter dataSourceAdapter;
    private MetadataRetriever metadataRetriever;

    //    @PreDestroy
    public void destroy() {
        disconnect();
    }

    public void connect(ConnectionProfile profile) throws InvalidProfileException {
        DataSourceAdapter adapter = profile.createDataSourceAdapter();
        adapter.validateProfile();

        disconnect();

        dataSourceAdapter = adapter;
        connectionProfile = profile;
    }

    public synchronized void disconnect() {
        LOG.info("cleaning up...");
        connectionProfile = null;

        if (metadataRetriever != null) {
            try {
                metadataRetriever.close();
                metadataRetriever = null;
            } catch (IOException e) {
                LOG.warn("failed to close metadata retriever", e);
            }
        }

        if (dataSourceAdapter != null) {
            try {
                dataSourceAdapter.close();
                dataSourceAdapter = null;
            } catch (Exception ex) {
                LOG.warn("failed to close the data source", ex);
            }
        }
    }

    public ConnectionProfile getConnectionProfile() {
        return connectionProfile;
    }

    public Connection getConnection() throws SQLException {
        return dataSourceAdapter.getDataSource().getConnection();
    }

    public DataSource getDataSource() {
        return dataSourceAdapter.getDataSource();
    }

    public synchronized MetadataRetriever getMetadata() throws SQLException {
        if (metadataRetriever == null) {
            metadataRetriever = new MetadataRetriever(getDataSource());
        }
        return metadataRetriever;
    }

    public void validateProfile(ConnectionProfile profile) throws InvalidProfileException {
        profile.createDataSourceAdapter().validateProfile();
    }

    public void doWithConnection(ConnectionAwareAction action) {
        Connection connection = null;
        try {
            connection = getConnection();
            action.execute(connection);
        } catch (SQLException e) {
            LOG.warn("action failed", e);
        } finally {
            JdbcUtils.close(connection);
        }
    }

    public <T> T getSingleResult(final String sql) {
        final AtomicReference<T> reference = new AtomicReference<T>();
        doWithConnection(new ConnectionAwareAction() {
            @Override
            public void execute(Connection connection) throws SQLException {
                ResultSet resultSet = null;
                try {
                    resultSet = connection.createStatement().executeQuery(sql);
                    if (resultSet.first()) {
                        Object object = resultSet.getObject(1);
                        reference.set((T) object);
                    }
                } finally {
                    JdbcUtils.close(resultSet);
                }
            }
        });
        return reference.get();
    }
}
