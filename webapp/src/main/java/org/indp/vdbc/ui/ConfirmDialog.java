package org.indp.vdbc.ui;

import com.vaadin.ui.*;

import java.awt.event.ActionListener;

/**
 *
 */
public class ConfirmDialog extends Window {

    private final HorizontalLayout buttons;

    public static void confirmYesNo(String message, ActionListener onYes) {
        new ConfirmDialog(message).
                addYesButton(onYes).
                addNoButton().
                show();
    }

    private ConfirmDialog(String message) {
        setCaption("Confirm");
        setResizable(false);
        setModal(true);

        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);
        setContent(root);

        root.addComponent(new Label(message));

        buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        root.addComponent(buttons);
        root.setComponentAlignment(buttons, Alignment.BOTTOM_CENTER);
    }

    private ConfirmDialog addYesButton(final ActionListener listener) {
        buttons.addComponent(new Button("Yes", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                listener.actionPerformed(null);
                close();
            }
        }));
        return this;
    }

    private ConfirmDialog addNoButton() {
        buttons.addComponent(new Button("No", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        }));
        return this;
    }

    private void show() {
        UI.getCurrent().addWindow(this);
    }
}
