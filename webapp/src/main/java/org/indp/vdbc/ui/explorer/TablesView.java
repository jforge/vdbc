package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import org.indp.vdbc.services.DatabaseSessionManager;

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

        final DetailsPaneComponent detailsPaneComponent = new DetailsPaneComponent();

        TableSelectorView tableSelectorView = new TableSelectorView(databaseSessionManager);
        tableSelectorView.setDetailsListener(new DetailsListener() {

            @Override
            public void showDetails(ObjectDetails detailsComponent) {
                detailsComponent.setSizeFull();
                detailsPaneComponent.showDetails(detailsComponent);
            }
        });

        sp.setFirstComponent(tableSelectorView);
        sp.setSecondComponent(detailsPaneComponent);
        sp.setSplitPosition(300, UNITS_PIXELS);
        setCompositionRoot(sp);
    }

}
