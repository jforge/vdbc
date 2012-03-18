package org.indp.vdbc.ui.profile;

import org.indp.vdbc.model.config.ConnectionProfile;

import java.util.*;

/**
 *
 */
public interface ConnectionProfileManager<T extends ConnectionProfile> {

    Class<T> getProfileClass();

    T createConnectionProfile();
    
    ConnectionProfileDetailsPanel<T> createPropertiesPanel(ConnectionProfile profile, ConnectionProfileDetailsPanel.ProfileListFacade profileListFacade);

    ConnectionProfileLoginPanel<T> createLoginPanel(ConnectionProfile profile);

    String getName();


    public static class Lookup {

        private static final Map<Class<? extends ConnectionProfile>, ConnectionProfileManager> FACTORY_MAP;

        static {
            HashMap<Class<? extends ConnectionProfile>, ConnectionProfileManager> map = new HashMap<Class<? extends ConnectionProfile>, ConnectionProfileManager>();
            ServiceLoader<ConnectionProfileManager> loader = ServiceLoader.load(ConnectionProfileManager.class);
            for (ConnectionProfileManager factory : loader) {
                map.put(factory.getProfileClass(), factory);
            }
            FACTORY_MAP = Collections.unmodifiableMap(map);
        }

        public static <T extends ConnectionProfile> ConnectionProfileManager<T> find(Class<T> clazz) {
            return FACTORY_MAP.get(clazz);
        }

        public static Collection<ConnectionProfileManager> getAll() {
            return FACTORY_MAP.values();
        }

        private Lookup() {
        }
    }
}
