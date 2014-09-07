package org.indp.vdbc.ui.profile;

import org.indp.vdbc.model.config.ConnectionProfile;

import java.util.*;

public interface ConnectionProfileSupportService<T extends ConnectionProfile> {

    Class<T> getProfileClass();

    T createConnectionProfile();

    ConnectionProfileDetailsPanel<T> createPropertiesPanel(ConnectionProfile profile);

    ConnectionProfileLoginPanelFactory<T> createLoginPanel(ConnectionProfile profile);

    String getName();

    public static class Lookup {

        private static final Map<Class<? extends ConnectionProfile>, ConnectionProfileSupportService> FACTORY_MAP;

        static {
            Map<Class<? extends ConnectionProfile>, ConnectionProfileSupportService> map = new HashMap<>();
            ServiceLoader<ConnectionProfileSupportService> loader = ServiceLoader.load(ConnectionProfileSupportService.class);
            for (ConnectionProfileSupportService factory : loader) {
                map.put(factory.getProfileClass(), factory);
            }
            FACTORY_MAP = Collections.unmodifiableMap(map);
        }

        public static <T extends ConnectionProfile> ConnectionProfileSupportService<T> find(Class<T> clazz) {
            return FACTORY_MAP.get(clazz);
        }

        public static Collection<ConnectionProfileSupportService> getAll() {
            return FACTORY_MAP.values();
        }

        private Lookup() {
        }
    }
}
