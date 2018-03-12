package org.indp.vdbc.ui.profile.impl;

import com.vaadin.ui.Component;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import org.indp.vdbc.model.config.ConnectionProfile;
import org.indp.vdbc.model.config.JdbcConnectionProfile;
import org.indp.vdbc.ui.profile.ConnectionProfileLoginPanelFactory;

public class JdbcConnectionProfileLoginPanel extends ConnectionProfileLoginPanelFactory<JdbcConnectionProfile> {

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
    public Component createCompositionRoot() {
        JdbcConnectionProfile profile = getProfile();

        userName = styleEditable(new TextField("Username:", profile.getUser()));
        password = styleEditable(new PasswordField("Password:", profile.getPassword()));

        FocusableVerticalLayout form = new FocusableVerticalLayout(userName, password);
        form.setSpacing(true);
        form.setMargin(true);
        form.setFocusTarget(password);
        return form;
    }

    private static class FocusableVerticalLayout extends VerticalLayout implements Component.Focusable {

        private Focusable focusTarget;

        public FocusableVerticalLayout(Component... children) {
            super(children);
        }

        public void setFocusTarget(Focusable focusTarget) {
            this.focusTarget = focusTarget;
        }

        @Override
        public void focus() {
            if (focusTarget != null) {
                focusTarget.focus();
            }
        }

        @Override
        public int getTabIndex() {
            return 0;
        }

        @Override
        public void setTabIndex(int tabIndex) {
        }
    }
}
