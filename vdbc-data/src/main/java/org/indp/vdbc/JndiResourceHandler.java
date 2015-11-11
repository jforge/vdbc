package org.indp.vdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to handle jndi resources for injecting jdbc parameters, settings instances and auth-grants dynamically. (as long
 * as no reasonable custom OAuth2 mechanism or better is available to restrict access to vdbc).
 * 
 * <p>
 * By default (no vdbc-related names in jndi context) there is no change in any vdbc behaviour.
 * <p>
 * If vdbc-related names are availabe in jndi context, vdbc behaviour is changed concerning settings and connection profile view.
 * 
 * <p>
 * Vdbc-related names are expected to be available using the JEE context root "java:comp/env" followed by a /vdbc subcontext and
 * then simple resource aliases resp. and additional /jdbc subcontext to inject parameters for a conection profile. <br>
 * This is a reasonably JEE oriented (like java:comp/env/<resourcetype>/<resource-alias>) name schema for a simple and
 * servlet/session independent injecting mechanism for some rules and resources required for a custom vdbc deployment.
 * 
 * <p>
 * The main purpose is to inject a trigger to switch the Settings/Connect View, if vdbc is used in an environment (w/o SSO)
 * defining some rules for accessing arbitrary data sources (i.e. block access by anyone not authorized). <br>
 * One simple solution (no SSO mechanism, no OAuth2, no serverwide credentials, no other common security approaches etc.) is to
 * set a simple Auth Token in the JNDI registry (java:comp/env/vdbc/auth) with state "granted" or "restricted". <br>
 * If - let's say - a Vaadin DashBoard has a reasonable Login-Procedure and the environment authorizes a user to view/manage data
 * sources with vdbc, a successful login could set a "granted" token independently from session scopes etc.
 * 
 * <br>
 * java:comp/env/vdbc/settingsEditorEnabled can be used to let vdbc disable the settings editor (http://bitlama.github.io/vdbc/
 * (-Dvdbc.settings.editor-enabled=false))
 * 
 * <p>
 * The /jdbc deliberately is not a name for a DataSource instance, but a subcontext for the parameters to create DataSources
 * (driver,url,user,password). <br>
 * If you do not provide encryption for the password, please ensure an alternative protection for the process. <br>
 * If the password is encrypted, some should provide an authUri (java:comp/env/vdbc/authUri) containing an (secured) Uri to get
 * information about the (simple symmetric) encryption algorithm together with the secrect to decrypt the password before creating
 * the connection profile (TODO).
 * 
 * <p>
 * Please note that we do not want to compete with reasonable and more sophisticated security mechanisms for Vdbc. It supports a
 * simple use case with a simple solution, and actually should be replaced with "real" security as early as possible.
 * 
 * @author jforge
 */
public class JndiResourceHandler {
    private static final Logger LOG = LoggerFactory.getLogger(JndiResourceHandler.class);

    public static final String JNDI_DEFAULT_ENV = "java:/comp/env";
    public static final String JNDI_PREFIX_VDBC = JNDI_DEFAULT_ENV + "/vdbc";

    static Object getJndiResource(String resourceName) {
        Object resource = null;
        try {
            Context initialContext = new InitialContext();
            resource = initialContext.lookup(resourceName);
        } catch (NamingException e) {
            LOG.warn("failed to access jndi context", e);
        }

        return resource;
    }

    /**
     * Injects a VDBC ConnectionProfile instance in order to complement to the list of available profile.
     * 
     * @return ConnectionProfile instance using the jndi-injected jdbc parameters.
     */
    static ConnectionProfile getJndiVdbcConnectionProfile() {
        ConnectionProfile profile = null;
        try {
            Context initialContext = new InitialContext();
            Context vdbcJdbcContext = (Context) initialContext.lookup(JNDI_PREFIX_VDBC);

            // YES, JndiConnectionProfile is know, here: JdbcConnectionProfile with jndi props is used on purpose.

            String url = (String) vdbcJdbcContext.lookup("/jdbc/url");
            String driverClassname = (String) vdbcJdbcContext.lookup("/jdbc/driverClassname");
            String dialect = (String) vdbcJdbcContext.lookup("/jdbc/dialect");
            String user = (String) vdbcJdbcContext.lookup("/jdbc/user");
            String password = (String) vdbcJdbcContext.lookup("/jdbc/password");

            profile = new JdbcConnectionProfile(dialect + "-injected", dialect, driverClassname, url, user,
                    password, null);

        } catch (NamingException e) {
            LOG.warn("failed to access jndi context", e);
        }

        return profile;
    }

    /**
     * Gets the authorization flag using the auth token from jndi context. The auth token can be produced by the container's or
     * another webapp's authoriation mechanism. A better approach to delegation authorization would be OAuth2 (e.g. see
     * scribe-java for details).
     * 
     * <br>
     * If there is no such name, the use of vdbc is authorized by default. <br>
     * If there auth tag "granted", vdbc can be used. <br>
     * Otherwise, vdbc should be disabled for creating or using datasources.
     * 
     * @return access to vdbc is granted by jndi auth token true/false.
     */
    static boolean isJndiAccessGranted() {
        boolean jndiAccessGranted = true;
        try {
            Context initialContext = new InitialContext();
            Context webContext = (Context) initialContext.lookup(JNDI_PREFIX_VDBC);
            String resource = (String) webContext.lookup("/auth");
            jndiAccessGranted = resource == null || resource.equals("granted");
        } catch (NamingException e) {
            LOG.warn("failed to access jndi context", e);
        }
        return jndiAccessGranted;
    }

    /**
     * Gets an authorization url from jndi context to be used for decryption purposes with the jdbc password parameters.
     * 
     * @return authorization uri derived from jndi context.
     */
    static String getJndiAuthorizationUri() {
        String resource = null;
        try {
            Context initialContext = new InitialContext();
            Context webContext = (Context) initialContext.lookup(JNDI_PREFIX_VDBC);
            resource = (String) webContext.lookup("/authUri");
        } catch (NamingException e) {
            LOG.warn("failed to access jndi context", e);
        }

        return resource;
    }

    /**
     * Gets the settingsEditorEnabled flag from jndi context to enable/disable the vdbc settings editor using the vm parameters
     * "vdbc.settings.editor-enabled" (true/false).
     * 
     * @see <a href="http://bitlama.github.io/vdbc/">Vdbc Docs</a>
     * 
     * @return settingsEditorEnabled is set to true/false using jndi.
     */
    static boolean isJndiSettingsEditorEnabled() {
        boolean settingsEditorEnabled = true;
        try {
            Context initialContext = new InitialContext();
            Context webContext = (Context) initialContext.lookup(JNDI_PREFIX_VDBC);
            String resource = (String) webContext.lookup("/settingsEditorEnabled");
            if (resource != null && "false".equalsIgnoreCase(resource.trim())) {
                settingsEditorEnabled = false;
            }
        } catch (NamingException e) {
            LOG.warn("failed to access jndi context", e);
        }

        // set the vm parameter outside this method.
        return settingsEditorEnabled;
    }
}
