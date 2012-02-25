package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import org.indp.vdbc.DatabaseSessionManager;

/**
 *
 *
 */
public class TablesView extends CustomComponent {

    public TablesView(DatabaseSessionManager databaseSessionManager) {
        setCaption("Tables");

        setSizeFull();
        final HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setSizeFull();

        TableSelectorView tableSelectorView = new TableSelectorView(databaseSessionManager);
        tableSelectorView.setDetailsListener(new DetailsListener() {

            @Override
            public void showDetails(Component component) {
                component.setSizeFull();
                sp.setSecondComponent(component);
            }
        });

        sp.setFirstComponent(tableSelectorView);
        sp.setSplitPosition(300, UNITS_PIXELS);
        setCompositionRoot(sp);
    }

}
