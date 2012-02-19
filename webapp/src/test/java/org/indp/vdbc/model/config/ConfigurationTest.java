package org.indp.vdbc.model.config;

import org.junit.Test;

import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class ConfigurationTest {

    @Test
    public void testMarshal() {
        Configuration c = new Configuration();
        c.addProfile(new JdbcConnectionProfile("1", "d1", "url1", "u1", "p1"));
        c.addProfile(new JdbcConnectionProfile("2", "d2", "url2", "u2", "p2"));
        c.addProfile(new JndiConnectionProfile("jdbc/DS"));

        StringWriter writer = new StringWriter();
        JAXB.marshal(c, writer);

        assertTrue(writer.toString().replace('\n', ' ').matches(".*<vdbc-config>\\s*<profiles>\\s*" +
                "<jdbc password=\"p1\" user=\"u1\" url=\"url1\" driver=\"d1\" name=\"1\"/>\\s*" +
                "<jdbc password=\"p2\" user=\"u2\" url=\"url2\" driver=\"d2\" name=\"2\"/>\\s*" +
                "<jndi jndiName=\"jdbc/DS\" name=\"jdbc/DS\"/>\\s*" +
                "</profiles>\\s*" +
                "</vdbc-config>\\s*"));

        Configuration configuration = JAXB.unmarshal(new StringReader(writer.toString()), Configuration.class);
        assertNotNull(configuration);
        List<ConnectionProfile> profiles = configuration.getProfiles();
        assertEquals(3, profiles.size());

        assertTrue(profiles.get(0) instanceof JdbcConnectionProfile);
        assertEquals("1", profiles.get(0).getName());

        assertTrue(profiles.get(1) instanceof JdbcConnectionProfile);
        assertEquals("2", profiles.get(1).getName());

        assertTrue(profiles.get(2) instanceof JndiConnectionProfile);
        assertEquals("jdbc/DS", profiles.get(2).getName());
    }
}
