package org.indp.vdbc.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 *
 * @author pi
 */
public class Toolbar extends CustomComponent {

    private final HorizontalLayout content;

    public Toolbar() {
        content = new HorizontalLayout();
//        content.setSizeFull();
        setCompositionRoot(content);
        addStyleName("toolbar");
    }

    @Override
    public void addComponent(Component component) {
        this.addComponent(component, Alignment.MIDDLE_LEFT);
    }

    public void addComponent(Component component, Alignment alignment) {
        content.addComponent(component);
        content.setComponentAlignment(component, alignment);
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
