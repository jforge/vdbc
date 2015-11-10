package org.indp.vdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.indp.vdbc.model.config.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JNDI Resource Handler Tester for injecting resources limiting access to and providing properties for datasources.
 * 
 * @author pi
 */
public class JndiResourceHandlerTest {
    private static final Logger LOG = LoggerFactory.getLogger(JndiResourceHandlerTest.class);

    static {
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            // tomcat-catalina:
            // System.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
            // System.setProperty(Context.URL_PKG_PREFIXES,"org.apache.naming");

            // jetty:
            // java.naming.factory.url.pkgs=org.eclipse.jetty.jndi
            // java.naming.factory.initial=org.eclipse.jetty.jndi.InitialContextFactory

            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.eclipse.jetty.jndi.InitialContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, "org.eclipse.jetty.jndi");

            InitialContext ic = new InitialContext();
            // ic.createSubcontext("java:");
            // ic.createSubcontext("java:/comp");
            ic.createSubcontext("java:/comp/env");
            ic.createSubcontext("java:/comp/env/jdbc");
            ic.createSubcontext("java:/comp/env/vdbc");

            // Construct DataSource
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:mem:db"); // dbc:h2:.vdbc-db/testdb;AUTO_SERVER=TRUE
            // ds.setURL("jdbc:oracle:thin:@host:port:db");
            // ds.setUser("sa");
            // ds.setPassword("sa");
            ic.bind("java:/comp/env/jdbc/vdbc", ds);

            ic.bind("java:/comp/env/vdbc/auth", "granted");
            ic.bind("java:/comp/env/vdbc/configuration", new Configuration());

        } catch (NamingException ex) {
            LOG.error("", ex);
        }
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testJndiResource() throws SQLException {
        Object resource = JndiResourceHandler.getJndiResource("java:/comp/env/jdbc/vdbc");
        assertNotNull(resource);
        LOG.info(resource.toString());
    }

    @Test
    public void testJndiAuthToken() throws SQLException {
        String resource = JndiResourceHandler.getJndiAuthorizationTag();
        assertNotNull(resource);
        LOG.info(resource.toString());
        assertEquals("granted", resource);
    }

    @Test
    public void testJndiVdbcConfiguration() throws SQLException {
        Configuration resource = JndiResourceHandler.getJndiVdbcConfiguration();
        assertNotNull(resource);
        LOG.info(resource.toString());
    }

    @Test
    public void testJndiDatasource() throws SQLException {
        DataSource resource = JndiResourceHandler.getJndiDatasource();
        assertNotNull(resource);
        LOG.info(resource.toString());

        try (Connection connection = resource.getConnection()) {
            assertNotNull(connection);
            LOG.info(connection.toString());
            assertTrue(connection.toString().contains("url=jdbc:h2:mem:db"));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}
