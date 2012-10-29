package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.JndiConnectionProfile;
import org.indp.vdbc.ui.profile.impl.fields.DialectFieldFactory;
import org.indp.vdbc.ui.profile.config.ProfileField;

/**
 *
 */
public class JndiConnectionProfileDetailsPanel extends AbstractConnectionProfileDetailsPanel<JndiConnectionProfile> {

    public JndiConnectionProfileDetailsPanel(JndiConnectionProfile profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    @Override
    protected DetailsForm createDetailsComponent() {
        return new DetailsForm(getProfile(),
                new ProfileField("name"),
                new ProfileField("dialect", new DialectFieldFactory("Dialect", true)),
                new ProfileField("jndiName", "JNDI Name", true));
    }
}
