package org.indp.vdbc.ui.profile;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.model.config.ConnectionProfile;

public abstract class ConnectionProfileLoginPanelFactory<T extends ConnectionProfile> {

    private final T profile;

    public abstract ConnectionProfile createConnectionProfile();

    public abstract Component createCompositionRoot();

    public ConnectionProfileLoginPanelFactory(T profile) {
        this.profile = profile;
    }

    protected <TField extends AbstractTextField> TField styleReadOnly(TField field) {
        field.setReadOnly(true);
        field.setWidth("100%");
//        field.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        field.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        return field;
    }

    protected <TField extends AbstractTextField> TField styleEditable(TField field) {
        field.setWidth("100%");
        return field;
    }

    protected T getProfile() {
        return profile;
    }
}
