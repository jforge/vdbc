package org.indp.vdbc.ui.workspace;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

/**
 *
 */
public abstract class HeaderComponent extends HorizontalLayout {
    private final WorkspacePageComponent workspacePageComponent;
    protected static final ThemeResource MENU_ICON = new ThemeResource("../runo/icons/16/arrow-down.png");
    // todos
//    protected static final ThemeResource CLOSE_ICON = new ThemeResource("../runo/icons/16/arrow-down.png");

    protected abstract void headerClicked(Button.ClickEvent event);

    protected abstract void close();

    HeaderComponent(WorkspacePageComponent workspacePageComponent) {
        this.workspacePageComponent = workspacePageComponent;
        setSpacing(false);
        setMargin(false);
        setStyleName("header-bar-header");
        createContent();
    }

    public WorkspacePageComponent getWorkspacePageComponent() {
        return workspacePageComponent;
    }

    private void createContent() {
        MenuBar menuBar = new MenuBar();
        MenuBar.MenuItem menuRoot = menuBar.addItem("", MENU_ICON, null);

        // tmp
        {
            menuRoot.addItem("Test", new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    Notification.show("ok");
                }
            });
        }

        Button titleButton = new Button(workspacePageComponent.getCaption(), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                headerClicked(event);
            }
        });
        titleButton.setStyleName(BaseTheme.BUTTON_LINK);
        titleButton.addStyleName("header-bar-header-title");

        // todo use icon
        Button closeButton = new NativeButton("×", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        closeButton.setStyleName("header-bar-close-button");
        closeButton.setDescription("Close");
//        closeButton.setIcon(CLOSE_ICON);

        addComponents(menuBar, titleButton, closeButton);
        setComponentAlignment(menuBar, Alignment.MIDDLE_CENTER);
        setComponentAlignment(titleButton, Alignment.MIDDLE_CENTER);
        setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);
    }
}
