package org.indp.vdbc.model.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pi
 */
@XmlRootElement(name = "vdbc-config")
@XmlAccessorType(XmlAccessType.NONE)
public class Configuration {

    @XmlElementWrapper(name = "profiles")
    @XmlElement(name = "profile")
    private List<ConnectionProfile> profiles;

    public void addProfile(ConnectionProfile profile) {
        if (null == profiles)
            profiles = new ArrayList<ConnectionProfile>();
        profiles.add(profile);
    }

    public List<ConnectionProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ConnectionProfile> profiles) {
        this.profiles = profiles;
    }
}
