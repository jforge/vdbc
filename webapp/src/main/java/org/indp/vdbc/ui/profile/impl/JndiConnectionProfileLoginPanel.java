package org.indp.vdbc.ui.profile.impl;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JndiConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanel;
import org.indp.vdbc.ui.LabelField;

/**
 *
 */
public class JndiConnectionProfileLoginPanel extends ConnectionProfileLoginPanel<JndiConnectionProfile> {

    public JndiConnectionProfileLoginPanel(JndiConnectionProfile profile) {
        super(profile);
    }

    @Override
    public ConnectionProfile createConnectionProfile() {
        return getProfile();
    }

    @Override
    protected Component createCompositionRoot() {
        FormLayout root = new FormLayout();
        root.setSizeFull();

        LabelField name = new LabelField("JNDI Name:", getProfile().getJndiName());
        root.addComponent(name);

        return root;
    }
}

