package org.indp.vdbc.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

/**
 *
 *
 */
public class Toolbar extends CustomComponent {

    private final HorizontalLayout content;

    public Toolbar() {
        content = new HorizontalLayout();
        content.setWidth("100%");
        setCompositionRoot(content);
        addStyleName("toolbar");
    }

    @Override
    public void addComponent(Component component) {
        this.addComponent(component, Alignment.MIDDLE_LEFT, null);
    }

    public void addComponent(Component component, Alignment alignment) {
        addComponent(component, alignment, null);
    }

    public void addComponent(Component component, Alignment alignment, String styleName) {
        content.addComponent(component);
        content.setComponentAlignment(component, alignment);
        if (styleName != null) {
            component.setStyleName(styleName);
        }
    }

    public void addLinkButton(String caption, Alignment alignment, Button.ClickListener clickListener) {
        Button button = new Button(caption, clickListener);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        addComponent(button, null == alignment ? Alignment.MIDDLE_LEFT : alignment);
    }

    public void setSpacing(boolean enabled) {
        content.setSpacing(enabled);
    }
}
