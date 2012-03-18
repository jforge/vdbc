package org.indp.vdbc.ui.profile;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import org.indp.vdbc.model.config.ConnectionProfile;

/**
 *
 */
public abstract class ConnectionProfileLoginPanel<T extends ConnectionProfile> extends CustomComponent implements Component.Focusable {

    private final T profile;

    public abstract ConnectionProfile createConnectionProfile();

    protected abstract Component createCompositionRoot();

    public ConnectionProfileLoginPanel(T profile) {
        this.profile = profile;
        setCompositionRoot(createCompositionRoot());
    }

    protected T getProfile() {
        return profile;
    }

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
