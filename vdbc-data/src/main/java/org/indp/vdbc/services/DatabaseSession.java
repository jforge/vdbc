package org.indp.vdbc.services;

import org.hibernate.dialect.Dialect;
import org.indp.vdbc.ConnectionListener;
import org.indp.vdbc.model.DataSourceAdapter;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.util.MetadataRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public class DatabaseSession {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSession.class);

    private final ConnectionProfile connectionProfile;
    private final ConnectionListener connectionListener;
    private final DataSourceAdapter dataSourceAdapter;
    private final Dialect dialect;
    private MetadataRetriever metadataRetriever;

    private volatile boolean closed = false;

    public DatabaseSession(ConnectionProfile connectionProfile, ConnectionListener connectionListener) {
        this.connectionProfile = connectionProfile;
        this.connectionListener = connectionListener;
        this.dataSourceAdapter = connectionProfile.createDataSourceAdapter();
        this.dialect = DialectSupport.getDialect(connectionProfile.getDialect());
    }

    public synchronized void close() {
        assert !closed;

        LOG.info("cleaning up...");

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
            } catch (Exception ex) {
                LOG.warn("failed to close the data source", ex);
            }
        }

        closed = true;
        connectionListener.connectionClosed(this);
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

    public Dialect getDialect() {
        return dialect;
    }

    public synchronized MetadataRetriever getMetadata() throws SQLException {
        if (metadataRetriever == null) {
            metadataRetriever = new MetadataRetriever(getDataSource());
        }
        return metadataRetriever;
    }

    public boolean isClosed() {
        return closed;
    }
//    public void doWithConnection(ConnectionAwareAction action) {
//        Connection connection = null;
//        try {
//            connection = getConnection();
//            action.execute(connection);
//        } catch (SQLException e) {
//            LOG.warn("action failed", e);
//        } finally {
//            JdbcUtils.close(connection);
//        }
//    }

//    public <T> T getSingleResult(final String sql) {
//        final AtomicReference<T> reference = new AtomicReference<T>();
//        doWithConnection(new ConnectionAwareAction() {
//            @Override
//            public void execute(Connection connection) throws SQLException {
//                ResultSet resultSet = null;
//                try {
//                    resultSet = connection.createStatement().executeQuery(sql);
//                    if (resultSet.first()) {
//                        Object object = resultSet.getObject(1);
//                        reference.set((T) object);
//                    }
//                } finally {
//                    JdbcUtils.close(resultSet);
//                }
//            }
//        });
//        return reference.get();
//    }
}
