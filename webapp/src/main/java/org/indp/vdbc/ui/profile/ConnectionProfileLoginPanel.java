package org.indp.vdbc.ui.profile;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.model.config.ConnectionProfile;

public abstract class ConnectionProfileLoginPanel<T extends ConnectionProfile> extends CustomComponent implements Component.Focusable {

    private final T profile;

    public abstract ConnectionProfile createConnectionProfile();

    protected abstract Component createCompositionRoot();

    public ConnectionProfileLoginPanel(T profile) {
        this.profile = profile;
        setCompositionRoot(createCompositionRoot());
    }

    protected <TField extends AbstractTextField> TField styleReadOnly(TField field) {
        field.setReadOnly(true);
        field.setWidth("100%");
        field.addStyleName(ValoTheme.TEXTFIELD_SMALL);
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
