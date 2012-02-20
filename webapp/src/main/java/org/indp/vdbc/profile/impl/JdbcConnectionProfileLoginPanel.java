package org.indp.vdbc.profile.impl;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.profile.ConnectionProfileLoginPanel;
import org.indp.vdbc.ui.LabelField;

/**
 *
 */
public class JdbcConnectionProfileLoginPanel<T extends ConnectionProfile> extends ConnectionProfileLoginPanel<T> {

    private final JdbcConnectionProfile profile;
    private final TextField userName;
    private final PasswordField password;

    public JdbcConnectionProfileLoginPanel(JdbcConnectionProfile profile) {
        this.profile = profile;
        FormLayout root = new FormLayout();
        setCompositionRoot(root);

        root.setSizeFull();
        root.setSpacing(true);
        root.setMargin(false, false, true, false);

        LabelField driver = new LabelField("Driver:", profile.getDriver());
        LabelField url = new LabelField("URL:", profile.getUrl());
        userName = new TextField("Username:", profile.getUser());
        password = new PasswordField("Password:");

        userName.setWidth("100%");
        password.setWidth("100%");

        root.addComponent(driver);
        root.addComponent(url);
        root.addComponent(userName);
        root.addComponent(password);
    }

    @Override
    public ConnectionProfile createConnectionProfile() {
        return new JdbcConnectionProfile(profile.getName(), profile.getDriver(), profile.getUrl(),
                userName.getValue().toString(), password.getValue().toString());
    }

    @Override
    public void focus() {
        password.focus();
    }
}
