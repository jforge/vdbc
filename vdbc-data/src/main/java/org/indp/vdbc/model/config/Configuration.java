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
    @XmlAnyElement(lax = true)
    @XmlElementRefs({
            @XmlElementRef(name = "jdbc", type = JdbcConnectionProfile.class),
            @XmlElementRef(name = "jndi", type = JndiConnectionProfile.class)
    })
    private List<ConnectionProfile> profiles;


    public void addProfile(ConnectionProfile profile) {
        if (null == profiles) {
            profiles = new ArrayList<>();
        }
        profiles.add(profile);
    }

    public List<ConnectionProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ConnectionProfile> profiles) {
        this.profiles = profiles;
    }

    public void removeProfile(ConnectionProfile profile) {
        profiles.remove(profile);
    }
}
