package org.indp.vdbc.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 *
 *
 */
public class LabelField extends CustomComponent implements Property {

    private final Label label;

    public LabelField() {
        label = new Label();
        label.setWidth("100%");
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.addComponent(label);
        setCompositionRoot(hl);
    }

    @Override
    public void setDescription(String description) {
        label.setDescription(description);
    }

    @Override
    public Object getValue() {
        return label.getCaption();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
        label.setCaption(null != newValue ? newValue.toString() : "");
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }
}
