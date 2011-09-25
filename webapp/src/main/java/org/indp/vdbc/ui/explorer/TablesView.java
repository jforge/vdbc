package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.SplitPanel;
import org.indp.vdbc.DatabaseSessionManager;

/**
 *
 * @author pi
 */
public class TablesView extends CustomComponent {

    public TablesView(DatabaseSessionManager databaseSessionManager) {
        setCaption("Tables");

        setSizeFull();
        final SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.setSizeFull();
//        sp.setStyleName("small");

        TableSelectorView tableSelectorView = new TableSelectorView(databaseSessionManager);
        tableSelectorView.setDetailsListener(new DetailsListener() {

            @Override
            public void showDetails(Component component) {
                component.setSizeFull();
                sp.setSecondComponent(component);
            }
        });

        sp.setFirstComponent(tableSelectorView);
//        sp.setSecondComponent(new Panel("details"));
        sp.setSplitPosition(300, UNITS_PIXELS);
        setCompositionRoot(sp);
    }

}
