package org.indp.vdbc.ui.workspace;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

/**
 *
 */
public abstract class HeaderBarComponent extends HorizontalLayout {

    private static final String SELECTED_TAB_STYLE = "header-bar-header-selected";

    private HeaderComponent selectedComponent;

    protected abstract void onPageSelection(WorkspacePageComponent component);

    protected abstract void onPageClose(WorkspacePageComponent component);

    public void addPage(final WorkspacePageComponent component) {
        HeaderComponent c = new HeaderComponent(component) {

            @Override
            protected void headerClicked(Button.ClickEvent event) {
                select(this);
            }

            @Override
            protected void close() {
                HeaderBarComponent bar = HeaderBarComponent.this;
                int index = bar.getComponentIndex(this);
                onPageClose(component);
                bar.removeComponent(this);

                if (selectedComponent != this) {
                    return;
                }

                if (bar.getComponentCount() == 0) {
                    select(null);
                } else {
                    // closed first -> select first, else select next or prev
                    int newIndex;
                    if (index == 0) {
                        newIndex = 0;
                    } else if (index < bar.getComponentCount()) {
                        newIndex = index;
                    } else {
                        newIndex = index - 1;
                    }
                    Component newSelection = bar.getComponent(newIndex);
                    assert newSelection instanceof HeaderComponent;
                    select((HeaderComponent) newSelection);
                }
            }
        };
        addComponent(c);
        select(c);
    }

    private void select(HeaderComponent c) {
        for (Component component : this) {
            component.removeStyleName(SELECTED_TAB_STYLE);
        }
        if (c != null) {
            c.addStyleName(SELECTED_TAB_STYLE);
            onPageSelection(c.getWorkspacePageComponent());
        } else {
            onPageSelection(null);
        }
        selectedComponent = c;
    }
}
