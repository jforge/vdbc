package org.indp.vdbc.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

import java.awt.event.ActionListener;

public class ConfirmDialog extends Window {
    private final HorizontalLayout buttons;

    public static void confirmYesNo(String message, ActionListener onYes) {
        new ConfirmDialog(message).
                addYesButton(onYes).
                addNoButton().
                show();
    }

    public static void confirmYesNo(String message, String yes, String no, ActionListener onYes) {
        new ConfirmDialog(message).
                addYesButton(yes, onYes).
                addNoButton(no).
                show();
    }

    private ConfirmDialog(String message) {
        buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        VerticalLayout root = new VerticalLayout(new Label(message), buttons);
        root.setSpacing(true);
        root.setMargin(true);
        root.setComponentAlignment(buttons, Alignment.BOTTOM_CENTER);

        setContent(root);
        setCaption("Confirm");
        setResizable(false);
        setDraggable(false);
        setClosable(false);
        setModal(true);
    }

    private ConfirmDialog addYesButton(final ActionListener listener) {
        return addYesButton("Yes", listener);
    }

    private ConfirmDialog addYesButton(String title, final ActionListener listener) {
        buttons.addComponent(new Button(title, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                listener.actionPerformed(null);
                close();
            }
        }));
        return this;
    }

    private ConfirmDialog addNoButton() {
        return addNoButton("No");
    }

    private ConfirmDialog addNoButton(String title) {
        buttons.addComponent(new Button(title, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        }));
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        return this;
    }

    private void show() {
        UI.getCurrent().addWindow(this);
        this.focus();
    }
}
