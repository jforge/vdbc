package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;
import com.vaadin.ui.components.colorpicker.ColorPickerPopup;
import org.indp.vdbc.ui.profile.config.AbstractProfileField;

public class ColorField extends AbstractProfileField {

    private Color color;
    private HorizontalLayout fieldContainer;
    private Label colorLabel;

    public ColorField(String id, String title, boolean required) {
        super(id, title, required);
    }

    @Override
    public HorizontalLayout getFieldComponent() {
        if (fieldContainer == null) {
            colorLabel = new Label();
//            colorLabel.setWidth("100px");
//            colorLabel.setHeight("50px");

            fieldContainer = new HorizontalLayout(colorLabel);
            fieldContainer.setCaption(getTitle());
            fieldContainer.setComponentAlignment(colorLabel, Alignment.MIDDLE_LEFT);
            fieldContainer.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    ColorPickerPopup popup = new ColorPickerPopup(color == null ? Color.WHITE : color);
                    popup.setModal(true);
                    popup.addColorChangeListener(new ColorChangeListener() {
                        @Override
                        public void colorChanged(ColorChangeEvent event) {
                            color = event.getColor();
                            refreshLabel();
                        }
                    });
                    UI.getCurrent().addWindow(popup);
                }
            });
        }
        return fieldContainer;
    }

    @Override
    public void readValue() {
        String colorString = getConnectionProfile().getColor();
        if (colorString != null && !colorString.isEmpty() && colorString.matches("[0-9a-zA-Z]{6}")) {
            color = decodeColor(colorString);
        }
        refreshLabel();
    }

    private void refreshLabel() {
        if (color == null) {
            colorLabel.setContentMode(ContentMode.TEXT);
            colorLabel.setValue("click here to set");
        } else {
            colorLabel.setContentMode(ContentMode.HTML);
            colorLabel.setValue("<div style=\"width: 30px; height: 20px; border: 1px solid gray; background-color: #" + encodeColor(color) + "\"></div>");
        }
    }

    @Override
    public void writeValue() {
        getConnectionProfile().setColor(encodeColor(color));
    }

    private String encodeColor(Color color) {
        return color == null ? "" : String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private Color decodeColor(String color) {
        Integer r = Integer.valueOf(color.substring(0, 2), 16);
        Integer g = Integer.valueOf(color.substring(2, 4), 16);
        Integer b = Integer.valueOf(color.substring(4, 6), 16);
        return new Color(r, g, b);
    }
}
