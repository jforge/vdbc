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
