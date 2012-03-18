package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.JdbcConnectionProfile;

/**
 *
 */
public class JdbcConnectionProfileDetailsPanel extends AbstractConnectionProfileDetailsPanel<JdbcConnectionProfile> {

    private static final String[] VISIBLE_PROPERTIES = {"name", "driver", "url", "user", "password", "validationQuery"};
    private static final String[] OPTIONAL_PROPERTIES = {"password", "validationQuery"};

    public JdbcConnectionProfileDetailsPanel(JdbcConnectionProfile profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    @Override
    protected DetailsForm createDetailsComponent() {
        return new DetailsForm(getProfile(), VISIBLE_PROPERTIES, OPTIONAL_PROPERTIES);
    }
}
