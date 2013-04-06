package org.indp.vdbc.ui.profile.config;

import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 */
public interface FormContext {

    AbstractProfileField getProfileField(String id);

    ConnectionProfile getConnectionProfile();
}
