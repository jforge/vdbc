package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.ColorPickerArea;
import com.vaadin.ui.HorizontalLayout;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ColorField extends AbstractProfileField {

    private static final Logger log = LoggerFactory.getLogger(ColorField.class);
    private AbstractColorPicker field;
    private HorizontalLayout fieldContainer;

    public ColorField(String id, String title, boolean required) {
        super(id, title, required);
    }

    @Override
    public HorizontalLayout getFieldComponent() {
        if (field == null) {
            field = new ColorPickerArea();
            fieldContainer = new HorizontalLayout(field);
            fieldContainer.setSpacing(false);
            fieldContainer.setMargin(false);
            fieldContainer.setCaption(getTitle());
            fieldContainer.setWidth("100%");
        }
        return fieldContainer;
    }

    @Override
    public void readValue() {
        String color = getConnectionProfile().getColor();
        if (color == null || color.isEmpty() || !color.matches("[0-9a-zA-Z]{6}")) {
            return;
        }

        field.setColor(decodeColor(color));
    }

    @Override
    public void writeValue() {
        getConnectionProfile().setColor(encodeColor(field.getColor()));
    }

    private String encodeColor(Color color) {
        return Integer.toHexString(color.getRed()) +
                Integer.toHexString(color.getGreen()) +
                Integer.toHexString(color.getBlue());
    }

    private Color decodeColor(String color) {
        Integer r = Integer.valueOf(color.substring(0, 2), 16);
        Integer g = Integer.valueOf(color.substring(2, 4), 16);
        Integer b = Integer.valueOf(color.substring(4, 6), 16);
        return new Color(r, g, b);
    }
}
