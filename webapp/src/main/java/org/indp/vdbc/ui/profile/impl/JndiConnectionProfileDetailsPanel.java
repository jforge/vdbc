package org.indp.vdbc.ui.profile.impl;

import org.indp.vdbc.model.config.JndiConnectionProfile;

/**
 *
 */
public class JndiConnectionProfileDetailsPanel extends AbstractConnectionProfileDetailsPanel<JndiConnectionProfile> {

    private static final String[] VISIBLE_PROPERTIES = {"name", "dialect", "jndiName"};
    private static final String[] OPTIONAL_PROPERTIES = {};

    public JndiConnectionProfileDetailsPanel(JndiConnectionProfile profile, ProfileListFacade profileListFacade) {
        super(profile, profileListFacade);
    }

    @Override
    protected DetailsForm createDetailsComponent() {
        return new DetailsForm(getProfile(), VISIBLE_PROPERTIES, OPTIONAL_PROPERTIES);
    }

}
