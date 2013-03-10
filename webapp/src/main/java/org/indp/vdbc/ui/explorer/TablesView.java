package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.ModuleContentComponent;

/**
 *
 *
 */
public class TablesView extends ModuleContentComponent {

    private final DatabaseSession databaseSession;

    public TablesView(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
    }

    @Override
    protected Component createContent() throws Exception {
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

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSizeFull();
        splitPanel.setFirstComponent(tableSelector);
        splitPanel.setSecondComponent(detailsPaneComponent);
        splitPanel.setSplitPosition(300, Unit.PIXELS);
        return splitPanel;
    }

    @Override
    protected String getTitle() {
        return "Tables";
    }

    @Override
    protected void close() {
    }
}
