package org.indp.vdbc.ui;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import java.awt.event.ActionListener;

/**
 *
 *
 */
public class ActionListenerAdapter implements ClickListener {

    protected ActionListener actionListener;

    @Override
    public void buttonClick(ClickEvent event) {
        if (null != actionListener)
            actionListener.actionPerformed(null);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
