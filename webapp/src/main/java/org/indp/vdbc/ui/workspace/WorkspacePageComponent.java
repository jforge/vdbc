package org.indp.vdbc.ui.workspace;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class WorkspacePageComponent extends CustomComponent {

    private static final Logger log = LoggerFactory.getLogger(WorkspacePageComponent.class);

    protected abstract Component createContent() throws Exception;

    protected abstract void close();

    @Override
    public void attach() {
        super.attach();
        if (getCompositionRoot() == null) {
            try {
                Component component = createContent();
                setSizeFull();
                setCompositionRoot(component);
            } catch (Exception e) {
                log.error("view creation failed", e);
                setCompositionRoot(new Label(e.getMessage()));
            }
        }
    }
}
