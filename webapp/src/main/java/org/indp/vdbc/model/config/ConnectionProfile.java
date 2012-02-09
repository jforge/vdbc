package org.indp.vdbc.model.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ConnectionProfile {

    @XmlAttribute
    private String name;

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

    public ConnectionProfile() {
    }

    public ConnectionProfile(String name, String driver, String url, String user, String password) {
        this.name = name;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionProfile that = (ConnectionProfile) o;

        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (validationQuery != null ? !validationQuery.equals(that.validationQuery) : that.validationQuery != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (validationQuery != null ? validationQuery.hashCode() : 0);
        return result;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
