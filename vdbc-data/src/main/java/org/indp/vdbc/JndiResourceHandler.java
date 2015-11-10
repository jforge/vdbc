package org.indp.vdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.indp.vdbc.model.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to handle jndi resources for injecting jdbc parameters, settings instances and auth-grants.
 * 
 * @author pi
 */
public class JndiResourceHandler {
    private static final Logger LOG = LoggerFactory.getLogger(JndiResourceHandler.class);

    static Object getJndiResource(String resourceName) {
        Object resource = null;
        try {
            Context initialContext = new InitialContext();
            resource = initialContext.lookup(resourceName);
        } catch (NamingException e) {
            LOG.warn("accessing jndi context failed", e);

        }

        return resource;
    }

    static DataSource getJndiDatasource() {
        DataSource resource = null;
        try {
            Context initialContext = new InitialContext();
            Context webContext = (Context) initialContext.lookup("java:/comp/env");
            resource = (DataSource) webContext.lookup("jdbc/vdbc");
        } catch (NamingException e) {
            LOG.warn("accessing jndi context failed", e);
        }

        return resource;
    }

    /**
     * Injects a VDBC Configuration instance override local configuration elements from user.dir.
     * 
     * @return Configuration instance overriding (browser-)local configurations.
     */
    static Configuration getJndiVdbcConfiguration() {
        Configuration resource = null;
        try {
            Context initialContext = new InitialContext();
            Context webContext = (Context) initialContext.lookup("java:/comp/env");
            resource = (Configuration) webContext.lookup("vdbc/configuration");
        } catch (NamingException e) {
            LOG.warn("accessing jndi context failed", e);
        }

        return resource;
    }

    /**
     * Gets the authorization tag from jndi context. Such a tag can be produced by the container's or another webapp's
     * authoriation mechanism. A better approach to delegation authorization would be OAuth2 (e.g. see scribe-java for details).
     * 
     * <br>
     * If there is no such name, the use of vdbc is authorized by default. <br>
     * If there auth tag "granted", vdbc can be used. <br>
     * Otherwise, vdbc is disable for creating oder using datasources
     * 
     * @return authorization tag derived from jndi context.
     */
    static String getJndiAuthorizationTag() {
        String resource = null;
        try {
            Context initialContext = new InitialContext();
            Context webContext = (Context) initialContext.lookup("java:/comp/env");
            resource = (String) webContext.lookup("vdbc/auth");
        } catch (NamingException e) {
            LOG.warn("accessing jndi context failed", e);
        }

        return resource;
    }
}
