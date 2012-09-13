package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import org.indp.vdbc.services.DatabaseSession;

/**
 *
 *
 */
public class TablesView extends CustomComponent {

    public TablesView(DatabaseSession databaseSession) {
        setCaption("Tables");

        setSizeFull();
        final HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setSizeFull();

        final DetailsPaneComponent detailsPaneComponent = new DetailsPaneComponent();

        TableSelectorComponent tableSelector = new TableSelectorComponent(databaseSession);
        tableSelector.setDetailsListener(new DetailsListener() {

            @Override
            public void showDetails(ObjectDetails detailsComponent) {
                ObjectDetails selectedDetailComponent = detailsPaneComponent.getSelectedDetailComponent();
                if (selectedDetailComponent != null) {
                    detailsComponent.setDetailsState(selectedDetailComponent.getDetailsState());
                }

                detailsComponent.setSizeFull();
                detailsPaneComponent.showDetails(detailsComponent);
            }
        });

        sp.setFirstComponent(tableSelector);
        sp.setSecondComponent(detailsPaneComponent);
        sp.setSplitPosition(300, UNITS_PIXELS);
        setCompositionRoot(sp);
    }

}
