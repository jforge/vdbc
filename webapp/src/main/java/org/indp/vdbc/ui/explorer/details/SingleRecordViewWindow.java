package org.indp.vdbc.ui.explorer.details;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.indp.vdbc.db.DialectSupport;

public class SingleRecordViewWindow extends Window {
    private final Item item;

    public SingleRecordViewWindow(Item item, final Runnable onClose) {
        this.item = item;
        if (onClose != null) {
            addCloseListener(new CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    onClose.run();
                }
            });
        }
    }

    @Override
    public void attach() {
        super.attach();

        setCaption("Record");
        setModal(true);
        setWidth("500px");
        setResizable(false);
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

        HorizontalLayout buttons = new HorizontalLayout(closeButton);
        buttons.setSpacing(true);
        buttons.setWidth("100%");
        buttons.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        VerticalLayout layout = new VerticalLayout(fields, buttons);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setExpandRatio(fields, 1f);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        return layout;
    }

    private Component createFields() {
        FormLayout form = new FormLayout();
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        form.setMargin(false);
        for (Object propertyId : item.getItemPropertyIds()) {
            if (!DialectSupport.isServiceColumn(propertyId.toString())) {
                Property property = item.getItemProperty(propertyId);
                form.addComponent(createField(propertyId, property));
            }
        }
        return form;
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
