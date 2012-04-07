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
        setCompositionRoot(content);
        addStyleName("toolbar");
    }

    @Override
    public void addComponent(Component c) {
        content.addComponent(c);
    }

    public void addLinkButton(String caption, Button.ClickListener clickListener) {
        Button button = new Button(caption, clickListener);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        addComponent(button);
    }

    public void setSpacing(boolean enabled) {
        content.setSpacing(enabled);
    }
}
