package org.indp.vdbc.ui.profile.impl;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JndiConnectionProfile;
import org.indp.vdbc.ui.ReadonlyTextField;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanel;

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

        TextField name = new ReadonlyTextField("JNDI Name:", getProfile().getJndiName());
        root.addComponent(name);

        return root;
    }
}

