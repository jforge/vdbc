package org.indp.vdbc.model.config;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
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
