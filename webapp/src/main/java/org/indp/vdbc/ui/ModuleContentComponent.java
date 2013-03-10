package org.indp.vdbc.ui;

import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class ModuleContentComponent extends CustomComponent {

    private static final Logger log = LoggerFactory.getLogger(ModuleContentComponent.class);

    protected abstract Component createContent() throws Exception;

    protected abstract String getTitle();

    protected abstract void close();

    // todo: getMenu()      -- to the left from the caption
    //       getToolbar()   -- to the right

    @Override
    public void attach() {
        super.attach();
        if (getCompositionRoot() == null) {
            try {
                Component component = createContent();
                String title = getTitle();

                Label titleLabel = new Label(title);
                titleLabel.addStyleName("module-title");
//                titleLabel.addStyleName(Reindeer.LABEL_H2);

                HorizontalLayout header = new HorizontalLayout(titleLabel);
                header.setWidth("100%");
                header.addStyleName("module-header");

                VerticalLayout layout = new VerticalLayout(header, component);
                layout.setExpandRatio(component, 1);
                layout.setSizeFull();
                setSizeFull();
                setCompositionRoot(layout);
            } catch (Exception e) {
                log.error("view creation failed", e);
                setCompositionRoot(new Label(e.getMessage()));
            }
        }
    }
}
