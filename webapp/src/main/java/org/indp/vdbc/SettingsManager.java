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
    protected static final String FILE_NAME = ".config/vdbc/vdbc-settings.xml";
    protected static final File SETTINGS_FILE = new File(System.getProperty("user.home") + File.separator + FILE_NAME);
    private Configuration configuration;

    public Configuration getConfiguration() {
        if (null == configuration) {
            if (!SETTINGS_FILE.exists())
                // TODO create and persist
                configuration = createDefaultConfiguration();
            else
                configuration = JAXB.unmarshal(SETTINGS_FILE, Configuration.class);
        }
        return configuration;
    }

    protected Configuration createDefaultConfiguration() {
        Configuration conf = new Configuration();
//        conf.addProfile(new ConnectionProfile("edump", "org.h2.Driver", "jdbc:h2:tcp://localhost/edump", "sa", ""));
//        conf.addProfile(new ConnectionProfile("derby sample db", "org.apache.derby.jdbc.ClientDriver", "jdbc:derby://localhost:1527/sample", "app", ""));
        conf.addProfile(new ConnectionProfile("H2 in memory", "org.h2.Driver", "jdbc:h2:mem:db", "sa", ""));
//        conf.addProfile(new ConnectionProfile("H2 in memory 2", "org.h2.Driver", "jdbc:h2:mem:db2", "sa", ""));
        return conf;
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
}
