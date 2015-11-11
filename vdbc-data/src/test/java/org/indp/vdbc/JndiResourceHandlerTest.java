package org.indp.vdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.h2.Driver;
import org.indp.vdbc.exceptions.InvalidProfileException;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.services.DatabaseSessionManager;
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

    private String dbTestUrl = "jdbc:h2:mem:db";
    private String dbTestDriver = Driver.class.getName();
    private String dbTestDialect = "h2";
    private String dbTestUser = "sa";
    private String dbTestPassword = "sx";

    @BeforeClass
    public static void setUpClass() throws Exception {
        // tomcat-catalina:
        // System.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
        // System.setProperty(Context.URL_PKG_PREFIXES,"org.apache.naming");

        // jetty:
        // java.naming.factory.url.pkgs=org.eclipse.jetty.jndi
        // java.naming.factory.initial=org.eclipse.jetty.jndi.InitialContextFactory

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.eclipse.jetty.jndi.InitialContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.eclipse.jetty.jndi");
    }

    @Before
    public void setUp() throws Exception {
        try {
            InitialContext ic = new InitialContext();
            ic.createSubcontext(JndiResourceHandler.JNDI_DEFAULT_ENV);
            ic.createSubcontext(JndiResourceHandler.JNDI_PREFIX_VDBC);
            ic.createSubcontext(JndiResourceHandler.JNDI_PREFIX_VDBC + "/jdbc");

            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/auth", "granted");
            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/authUri", "");
            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/settingsEditorEnabled", "false");
            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/jdbc/url", dbTestUrl);
            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/jdbc/driverClassname", dbTestDriver);
            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/jdbc/dialect", dbTestDialect);
            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/jdbc/user", dbTestUser);
            ic.bind(JndiResourceHandler.JNDI_PREFIX_VDBC + "/jdbc/password", dbTestPassword);
        } catch (NameAlreadyBoundException ex) {
            // ignore bind exception during tearDown
        } catch (NamingException ex) {
            LOG.error("", ex);
        }
    }

    @After
    public void tearDown() throws Exception {
        InitialContext ic = new InitialContext();
        if (ic != null) {
            unbindAll(ic);
            ic.close();
        }
    }

    private void unbindAll(Context context) {
        try {
            NamingEnumeration<Binding> bindings = context.listBindings("");
            List<String> names = new ArrayList<String>();
            LOG.debug(names.toString());
            while (bindings.hasMoreElements()) {
                Binding next = bindings.nextElement();
                names.add(next.getName());
            }
            for (String name : names) {
                context.unbind(name);
                LOG.debug(name);
            }
        } catch (NamingException e) {
            // ignore unbind exception during tearDown
        }

        try {
            context.unbind(JndiResourceHandler.JNDI_DEFAULT_ENV);
        } catch (NamingException e) {
            // ignore unbind exception during tearDown
        }
    }

    @Test
    public void testJndiResource() throws SQLException {
        Object resource = JndiResourceHandler.getJndiResource(JndiResourceHandler.JNDI_PREFIX_VDBC);
        assertNotNull(resource);
        LOG.info(resource.toString());
    }

    @Test
    public void testJndiAuthToken() throws SQLException {
        boolean authorized = JndiResourceHandler.isJndiAccessGranted();
        LOG.info("authorized = " + authorized);
        assertTrue(authorized);
    }

    @Test
    public void testJndiAuthUri() throws SQLException {
        String resource = JndiResourceHandler.getJndiAuthorizationUri();
        assertNotNull(resource);
        LOG.info(resource.toString());
        assertEquals("", resource);
    }

    @Test
    public void testJndiSettingsEditorEnabled() throws SQLException {
        boolean settingsEditorEnabled = JndiResourceHandler.isJndiSettingsEditorEnabled();
        LOG.info("settingsEditorEnabled = " + settingsEditorEnabled);
        assertFalse(settingsEditorEnabled);
    }

    @Test
    public void testJndiVdbcJdbc() throws SQLException {
        ConnectionProfile profile = JndiResourceHandler.getJndiVdbcConnectionProfile();
        LOG.info(profile.toString());
        assertEquals(dbTestDialect + "-injected", profile.getName());
        assertEquals(dbTestDialect, profile.getDialect());
        assertNull(profile.getColor());

        assertTrue(profile instanceof JdbcConnectionProfile);

        assertEquals(dbTestUrl, ((JdbcConnectionProfile) profile).getUrl());
        assertEquals(dbTestDriver, ((JdbcConnectionProfile) profile).getDriver());
        assertEquals(dbTestUser, ((JdbcConnectionProfile) profile).getUser());
        assertEquals(dbTestPassword, ((JdbcConnectionProfile) profile).getPassword());

        DatabaseSessionManager databaseSessionManager = new DatabaseSessionManager(new ConnectionListener() {
            @Override
            public void connectionEstablished(DatabaseSession databaseSession) {
                assertNotNull(databaseSession);
                assertEquals(dbTestDialect, databaseSession.getDialect().getId());
            }

            @Override
            public void connectionClosed(DatabaseSession databaseSession) {
                assertNotNull(databaseSession);
                assertEquals(dbTestDialect, databaseSession.getDialect().getId());
            }
        });

        try {
            databaseSessionManager.validateProfile(profile);
        } catch (InvalidProfileException ex) {
            fail(ex.getMessage());
        }

        DatabaseSession dbSession = null;
        try {
            dbSession = databaseSessionManager.createSession(profile);
            assertNotNull(dbSession);
        } catch (Exception ex) {
            fail(ex.getMessage());
        } finally {
            if (dbSession != null && !dbSession.isClosed()) {
                dbSession.close();
            }
        }
    }

}
