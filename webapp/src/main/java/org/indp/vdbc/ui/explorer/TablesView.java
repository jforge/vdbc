package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.workspace.WorkspacePageComponent;

/**
 *
 *
 */
public class TablesView extends WorkspacePageComponent {

    private final DatabaseSession databaseSession;

    public TablesView(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
        setCaption("Tables");
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
    protected void close() {
    }
}
