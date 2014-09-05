package org.indp.vdbc.ui.profile.impl;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanel;

public class JdbcConnectionProfileLoginPanel extends ConnectionProfileLoginPanel<JdbcConnectionProfile> {

    private TextField userName;
    private PasswordField password;

    public JdbcConnectionProfileLoginPanel(JdbcConnectionProfile profile) {
        super(profile);
    }

    @Override
    public ConnectionProfile createConnectionProfile() {
        JdbcConnectionProfile profile = getProfile();
        return new JdbcConnectionProfile(profile.getName(), profile.getDialect(), profile.getDriver(), profile.getUrl(),
                userName.getValue(), password.getValue(), profile.getColor());
    }

    @Override
    protected Component createCompositionRoot() {
        JdbcConnectionProfile profile = getProfile();

        TextField driver = styleReadOnly(new TextField("Driver:", profile.getDriver()));
        TextField url = styleReadOnly(new TextField("URL:", profile.getUrl()));

        userName = styleEditable(new TextField("Username:", profile.getUser()));
        password = styleEditable(new PasswordField("Password:", profile.getPassword()));

        FormLayout root = new FormLayout(driver, url, userName, password);
        root.setSizeFull();
//        root.setSpacing(false);
        return root;
    }

    @Override
    public void focus() {
        password.focus();
    }
}
