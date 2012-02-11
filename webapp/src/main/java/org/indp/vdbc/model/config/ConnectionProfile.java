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
