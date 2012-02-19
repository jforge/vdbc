package org.indp.vdbc.model.config;

import org.indp.vdbc.model.DataSourceAdapter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

/**
 *
 */
@XmlRootElement(name = "jndi")
@XmlAccessorType(XmlAccessType.NONE)
public class JndiConnectionProfile extends ConnectionProfile {

    @XmlAttribute
    private String jndiName;

    public JndiConnectionProfile() {
    }

    public JndiConnectionProfile(String jndiName) {
        this.jndiName = jndiName;
        setName(jndiName);
    }

    @Override
    public String getConnectionPresentationString() {
        return "jndi:" + getJndiName();
    }

    @Override
    public DataSourceAdapter createDataSourceAdapter() {
        return new Adapter();
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    private class Adapter implements DataSourceAdapter {
        private DataSource dataSource;

        @Override
        public synchronized DataSource getDataSource() {
            if (dataSource == null) {
                dataSource = lookup();
            }
            return dataSource;
        }

        @Override
        public boolean isValidProfile() {
            return lookup() != null;
        }

        @Override
        public void close() throws IOException {
        }

        private DataSource lookup() {
            try {
                InitialContext context = new InitialContext();
                return (DataSource) context.lookup(getJndiName());
            } catch (NamingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
