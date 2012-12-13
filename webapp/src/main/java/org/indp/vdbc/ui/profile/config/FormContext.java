package org.indp.vdbc.ui.profile.config;

import com.vaadin.ui.Field;
import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 */
public interface FormContext {

    Field getField(String id);

    ConnectionProfile getConnectionProfile();
}
