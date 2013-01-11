package org.indp.vdbc.ui;

import com.vaadin.ui.TextField;

/**
 *
 */
public class ReadonlyTextField extends TextField {
    public ReadonlyTextField(String caption, String value) {
        super(caption, value);
        setReadOnly(true);
        setWidth("100%");
    }
}
