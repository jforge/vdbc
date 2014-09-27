package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.HorizontalSplitPanel;
import org.indp.vdbc.services.DatabaseSession;

import java.sql.SQLException;

public class TablesView extends HorizontalSplitPanel {

    public TablesView(DatabaseSession databaseSession) throws SQLException {
        final DetailsPaneComponent detailsPaneComponent = new DetailsPaneComponent();
        TableListComponent tableSelector = new TableListComponent(databaseSession);
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
        setFirstComponent(tableSelector);
        setSecondComponent(detailsPaneComponent);
        setSplitPosition(300, Unit.PIXELS);
        setCaption("Tables");
    }
}
