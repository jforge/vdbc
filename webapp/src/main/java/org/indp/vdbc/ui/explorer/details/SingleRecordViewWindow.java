package org.indp.vdbc.ui.explorer.details;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 */
public class SingleRecordViewWindow extends Window {
    private final Item item;

    public SingleRecordViewWindow(Item item) {
        this.item = item;
    }

    @Override
    public void attach() {
        super.attach();

        setCaption("Record");
        setModal(true);
        setWidth("500px");
        setContent(createLayout());
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
    }

    private ComponentContainer createLayout() {
        Component fields = createFields();

        Button closeButton = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        closeButton.focus();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setMargin(true);
        buttons.setWidth("100%");
        buttons.addComponent(closeButton);
        buttons.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(false);
        layout.addComponent(fields);
        layout.addComponent(buttons);
        layout.setExpandRatio(fields, 1f);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        return layout;
    }

    private Component createFields() {
        FormLayout form = new FormLayout();
        form.setWidth("100%");
        for (Object propertyId : item.getItemPropertyIds()) {
            Property property = item.getItemProperty(propertyId);
            form.addComponent(createField(propertyId, property));
        }

        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setSizeFull();
        panel.setContent(form);
        return panel;
    }

    private Component createField(Object propertyId, Property property) {
        // todo check property type
        Object value = property.getValue();
        TextField field = new TextField(propertyId.toString(), value == null ? "(null)" : value.toString());
        field.setWidth("100%");
        field.setReadOnly(true);
        return field;
    }
}
