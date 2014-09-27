package org.indp.vdbc.util;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UnsafeCommand implements MenuBar.Command {

    private static final Logger log = LoggerFactory.getLogger(UnsafeCommand.class);

    protected abstract void menuSelectedImpl(MenuBar.MenuItem selectedItem) throws Exception;

    @Override
    public void menuSelected(MenuBar.MenuItem selectedItem) {
        try {
            menuSelectedImpl(selectedItem);
        } catch (Exception e) {
            log.warn("Action failed", e);
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
