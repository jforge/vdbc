package org.indp.vdbc;

import com.google.common.io.Files;
import org.indp.vdbc.model.config.Configuration;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
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
	
    public static final String VDBC_SETTINGS_EDITOR_ENABLED_PROPERTY = "vdbc.settings.editor-enabled";
    public static final String VDBC_EXPERIMENTS_ENABLED_PROPERTY = "vdbc.experiments.enabled";

    private static final Logger LOG = LoggerFactory.getLogger(SettingsManager.class);
    private static final String FILE_PATH = ".config" + File.separator + "vdbc";
    private static final File SETTINGS_DIR = new File(System.getProperty("user.home") + File.separator + FILE_PATH);
    private static final File SETTINGS_FILE = new File(SETTINGS_DIR, "vdbc-settings.xml");

    private Configuration configuration;

    private static final SettingsManager INSTANCE = new SettingsManager();

    public static SettingsManager get() {
        return INSTANCE;
    }

    public synchronized Configuration getConfiguration() {
        if (null == configuration) {
            if (!SETTINGS_FILE.exists()) {
                // TODO create and persist
                configuration = createDefaultConfiguration();
            } else {
                try {
                    configuration = JAXB.unmarshal(SETTINGS_FILE, Configuration.class);
                } catch (Exception e) {
                    configuration = createDefaultConfiguration();
                }
            }
        }
        return configuration;
    }

    public synchronized void persistConfiguration() {
        try {
            Files.createParentDirs(SETTINGS_FILE);
            OutputStream out = new FileOutputStream(SETTINGS_FILE);
            JAXB.marshal(configuration, out);
            out.close();
        } catch (Exception ex) {
            LOG.warn("failed to create settings file", ex);
        }
    }

    public boolean isSettingsEditorEnabled() {
        String settingsEditorEnabled = System.getProperty(VDBC_SETTINGS_EDITOR_ENABLED_PROPERTY);
        return settingsEditorEnabled == null || "true".equals(settingsEditorEnabled);
    }

    public boolean isExperimenting() {
        return Boolean.getBoolean(VDBC_EXPERIMENTS_ENABLED_PROPERTY);
    }

    private Configuration createDefaultConfiguration() {
        Configuration conf = new Configuration();
        conf.addProfile(new JdbcConnectionProfile("H2 in memory", "h2", "org.h2.Driver", "jdbc:h2:mem:db", "sa", "", null));
        return conf;
    }

    private SettingsManager() {
    }
}
