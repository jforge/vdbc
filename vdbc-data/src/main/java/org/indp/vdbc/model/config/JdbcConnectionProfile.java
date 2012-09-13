package org.indp.vdbc.model.config;

import com.google.common.base.Strings;
import org.apache.commons.dbcp.BasicDataSource;
import org.indp.vdbc.exceptions.InvalidProfileException;
import org.indp.vdbc.model.DataSourceAdapter;
import org.indp.vdbc.util.JdbcUtils;

import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "jdbc")
public class JdbcConnectionProfile extends ConnectionProfile {

    @XmlAttribute
    private String driver;

    @XmlAttribute
    private String url;

    @XmlAttribute
    private String user;

    @XmlAttribute
    private String password;

    @XmlAttribute
    private String validationQuery;

    public JdbcConnectionProfile() {
    }

    public JdbcConnectionProfile(String name, String dialect, String driver, String url, String user, String password) {
        setName(name);
        setDialect(dialect);
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public String getConnectionPresentationString() {
        return getUrl();
    }

    @Override
    public DataSourceAdapter createDataSourceAdapter() {
        return new Adapter();
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    private class Adapter implements DataSourceAdapter {

        private BasicDataSource dataSource;

        @Override
        public synchronized DataSource getDataSource() {
            if (dataSource == null) {
                dataSource = new BasicDataSource();
                dataSource.setDriverClassName(getDriver());
                dataSource.setUrl(getUrl());
                dataSource.setUsername(getUser());
                dataSource.setPassword(getPassword());

                if (!Strings.isNullOrEmpty(getValidationQuery())) {
                    dataSource.setValidationQuery(getValidationQuery());
                }

                dataSource.setMaxActive(32);
                dataSource.setMaxIdle(4);
                dataSource.setMaxWait(20 * 1000);
                dataSource.setMaxOpenPreparedStatements(8);
            }
            return dataSource;
        }

        @Override
        public void validateProfile() throws InvalidProfileException {
            try {
                Connection conn = JdbcUtils.getConnection(driver, url, user, password);
                JdbcUtils.close(conn);
            } catch (Exception e) {
                throw new InvalidProfileException(e.getMessage());
            }
        }

        @Override
        public void close() throws IOException {
            if (dataSource != null) {
                try {
                    dataSource.close();
                    dataSource = null;
                } catch (SQLException e) {
                    throw new IOException(e);
                }
            }
        }
    }
}
