package org.indp.vdbc.model.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author pi
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
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ConnectionProfile other = (ConnectionProfile) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if ((this.driver == null) ? (other.driver != null) : !this.driver.equals(other.driver))
            return false;
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url))
            return false;
        if ((this.user == null) ? (other.user != null) : !this.user.equals(other.user))
            return false;
        if ((this.password == null) ? (other.password != null) : !this.password.equals(other.password))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.driver != null ? this.driver.hashCode() : 0);
        hash = 23 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 23 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 23 * hash + (this.password != null ? this.password.hashCode() : 0);
        return hash;
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
}
