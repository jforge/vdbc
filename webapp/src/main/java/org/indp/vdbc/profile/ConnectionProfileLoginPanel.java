package org.indp.vdbc.profile;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 */
public abstract class ConnectionProfileLoginPanel<T extends ConnectionProfile> extends CustomComponent implements Component.Focusable {

    public abstract ConnectionProfile createConnectionProfile();

    @Override
    public int getTabIndex() {
        return 0;
    }

    @Override
    public void setTabIndex(int tabIndex) {
    }

    @Override
    public void focus() {
        super.focus();
    }
}
