package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.ui.profile.impl.fields.DialectFieldFactory;
import org.indp.vdbc.ui.profile.config.ProfileField;
import org.indp.vdbc.ui.profile.impl.fields.PasswordFieldFactory;

/**
 *
 */
public class JdbcConnectionProfileDetailsPanel extends AbstractConnectionProfileDetailsPanel<JdbcConnectionProfile> {

    public JdbcConnectionProfileDetailsPanel(JdbcConnectionProfile profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    @Override
    protected DetailsForm createDetailsComponent() {
        return new DetailsForm(getProfile(),
                new ProfileField("name"),
                new ProfileField("dialect", new DialectFieldFactory("Dialect", true)),
                new ProfileField("driver"),
                new ProfileField("url"),
                new ProfileField("user"),
                new ProfileField("password", new PasswordFieldFactory("Password", false)),
                new ProfileField("validationQuery", "Validation Query", false));
    }
}
