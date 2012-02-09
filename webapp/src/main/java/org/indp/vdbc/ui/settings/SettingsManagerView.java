package org.indp.vdbc.ui.settings;

import com.vaadin.ui.*;

/**
 *
 */
public class SettingsManagerView extends Window {

    private ListSelect list;
    private Panel panel;

    public SettingsManagerView() {
        setModal(true);
        setCaption("Settings");
        setWidth("600px");
        setHeight("400px");

        GridLayout layout = createMainLayout();
        setContent(layout);

        Component bottom = createBottomBar();
        layout.addComponent(bottom, 0, 1);
        layout.setComponentAlignment(bottom, Alignment.MIDDLE_RIGHT);
        layout.setRowExpandRatio(0, 1);
    }

    private GridLayout createMainLayout() {
        GridLayout layout = new GridLayout(1, 2);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(createWorkArea(), 0, 0);
        layout.setSizeFull();
        return layout;
    }

    private Component createWorkArea() {
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        split.setSizeFull();
        split.setSplitPosition(30);

        list = new ListSelect();
        list.setSizeFull();
        list.setNullSelectionAllowed(false);

        panel = new Panel();
        panel.setSizeFull();

        split.addComponent(list);
        split.addComponent(panel);

        return split;
    }

    private Component createBottomBar() {
        HorizontalLayout bottom = new HorizontalLayout();
        bottom.setWidth("100%");
        bottom.setSpacing(true);

        Button addButton = new Button("Add", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });

        Button removeButton = new Button("Remove", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });

        Button applyButton = new Button("Apply", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });

        Button closeButton = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

        bottom.addComponent(addButton);
        bottom.addComponent(removeButton);
        bottom.addComponent(applyButton);
        bottom.addComponent(closeButton);
        
        bottom.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);
        bottom.setExpandRatio(closeButton, 1);

        return bottom;
    }
}
