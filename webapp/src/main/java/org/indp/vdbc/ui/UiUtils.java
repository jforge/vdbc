package org.indp.vdbc.ui;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/**
 *
 *
 */
public final class UiUtils {

    public static Component createHorizontalSpacer(int width) {
        Label l = new Label();
        l.setWidth(width, Sizeable.Unit.PIXELS);
        return l;
    }

    private UiUtils() {
    }
}
