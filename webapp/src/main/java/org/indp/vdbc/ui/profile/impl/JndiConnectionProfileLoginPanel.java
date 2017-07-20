package org.indp.vdbc.ui.profile.impl;

import com.vaadin.ui.Component;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JndiConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanelFactory;

public class JndiConnectionProfileLoginPanel extends ConnectionProfileLoginPanelFactory<JndiConnectionProfile> {

    public JndiConnectionProfileLoginPanel(JndiConnectionProfile profile) {
        super(profile);
    }

    @Override
    public ConnectionProfile createConnectionProfile() {
        return getProfile();
    }

    @Override
    public Component createCompositionRoot() {
        TextField name = styleReadOnly(new TextField("JNDI Name:", getProfile().getJndiName()));
        VerticalLayout root = new VerticalLayout(name);
        root.setMargin(true);
        return root;
    }
}

