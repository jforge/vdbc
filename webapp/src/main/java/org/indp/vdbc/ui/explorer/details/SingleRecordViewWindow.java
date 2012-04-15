package org.indp.vdbc.ui.explorer.details;

import com.vaadin.ui.*;

/**
 *
 */
public class SingleRecordViewWindow extends Window {
    public SingleRecordViewWindow() {
        setCaption("Record");
        setModal(true);
        setContent(createLayout());
    }

    private ComponentContainer createLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        }));
        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        return layout;
    }
}
