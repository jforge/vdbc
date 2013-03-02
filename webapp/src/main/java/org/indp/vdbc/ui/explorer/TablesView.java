package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.HorizontalSplitPanel;
import org.indp.vdbc.services.DatabaseSession;

/**
 *
 *
 */
public class TablesView extends HorizontalSplitPanel {

    public TablesView(DatabaseSession databaseSession) {
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

        setSizeFull();
        setCaption("Tables");
        setFirstComponent(tableSelector);
        setSecondComponent(detailsPaneComponent);
        setSplitPosition(300, Unit.PIXELS);
    }
}
