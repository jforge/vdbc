package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;
import org.indp.vdbc.ui.profile.impl.fields.*;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class JdbcConnectionProfileDetailsPanel extends AbstractConnectionProfileDetailsPanel<JdbcConnectionProfile> {

    public JdbcConnectionProfileDetailsPanel(JdbcConnectionProfile profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    @Override
    protected List<AbstractProfileField> getFields() {
        return Arrays.asList(
                new SimpleProfileField("name"),
                new DialectField("dialect", "Dialect", true),
                new DriverField("driver", "Driver", true),
                new UrlField("url", "JDBC URL", true),
                new SimpleProfileField("user"),
                new PasswordField("password", "Password", false),
                new SimpleProfileField("validationQuery", "Validation Query", false));
    }
}
