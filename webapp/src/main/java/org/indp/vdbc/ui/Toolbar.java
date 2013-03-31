package org.indp.vdbc.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.indp.vdbc.util.UnsafeRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class Toolbar extends CustomComponent {
    private static final Logger log = LoggerFactory.getLogger(Toolbar.class);
    private final HorizontalLayout content;

    public Toolbar() {
        content = new HorizontalLayout();
        setCompositionRoot(content);
        addStyleName("toolbar");
    }

    public void addComponent(Component c) {
        content.addComponent(c);
    }

    public void addLinkButton(String caption, Button.ClickListener clickListener) {
        Button button = new Button(caption, clickListener);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        addComponent(button);
    }

    public void addLinkButton(String caption, final UnsafeRunnable clickListener) {
        addLinkButton(caption, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    clickListener.run();
                } catch (Exception e) {
                    log.warn("action failed", e);
                    Notification.show("Action failed: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
    }

    public void setSpacing(boolean enabled) {
        content.setSpacing(enabled);
    }
}
