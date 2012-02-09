package org.indp.vdbc;

import org.indp.vdbc.model.config.Configuration;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 *
 */
public class SettingsManager {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsManager.class);
    private static final String FILE_NAME = ".config/vdbc/vdbc-settings.xml";
    private static final File SETTINGS_FILE = new File(System.getProperty("user.home") + File.separator + FILE_NAME);

    private Configuration configuration;

    private static final SettingsManager INSTANCE = new SettingsManager();

    public static SettingsManager get() {
        return INSTANCE;
    }

    public synchronized Configuration getConfiguration() {
        if (null == configuration) {
            if (!SETTINGS_FILE.exists())
                // TODO create and persist
                configuration = createDefaultConfiguration();
            else
                configuration = JAXB.unmarshal(SETTINGS_FILE, Configuration.class);
        }
        return configuration;
    }

    public synchronized void persistConfiguration() {
        try {
            OutputStream out = new FileOutputStream(SETTINGS_FILE);
            JAXB.marshal(configuration, out);
            out.close();
        } catch (Exception ex) {
            LOG.warn("failed to create settings file", ex);
        }
    }

    private Configuration createDefaultConfiguration() {
        Configuration conf = new Configuration();
        conf.addProfile(new ConnectionProfile("H2 in memory", "org.h2.Driver", "jdbc:h2:mem:db", "sa", ""));
        return conf;
    }

    private SettingsManager() {
    }
}
