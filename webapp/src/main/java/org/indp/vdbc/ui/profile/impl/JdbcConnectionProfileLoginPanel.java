package org.indp.vdbc.ui.profile.impl;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.ui.ReadonlyTextField;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanel;

/**
 *
 */
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
                userName.getValue().toString(), password.getValue().toString());
    }

    @Override
    protected Component createCompositionRoot() {
        JdbcConnectionProfile profile = getProfile();

        TextField driver = new ReadonlyTextField("Driver:", profile.getDriver());
        TextField url = new ReadonlyTextField("URL:", profile.getUrl());

        userName = new TextField("Username:", profile.getUser());
        password = new PasswordField("Password:");

        userName.setWidth("100%");
        password.setWidth("100%");

        FormLayout root = new FormLayout();
        root.setSizeFull();
        root.setSpacing(true);
        root.setMargin(new MarginInfo(false, false, true, false));
        root.addComponent(driver);
        root.addComponent(url);
        root.addComponent(userName);
        root.addComponent(password);

        return root;
    }

    @Override
    public void focus() {
        password.focus();
    }
}
