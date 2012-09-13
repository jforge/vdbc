package org.indp.vdbc.ui.explorer.details;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.explorer.DetailsState;
import org.indp.vdbc.ui.explorer.ObjectDetails;

/**
 *
 *
 */
public class TableDetailsView extends CustomComponent implements ObjectDetails {

    private Property pinned;
    private final HorizontalLayout customToolbar = new HorizontalLayout();
    private final TabSheet tabSheet;

    public TableDetailsView(JdbcTable table, DatabaseSession databaseSession) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);

        tabSheet = createTabSheet(table, databaseSession);

        layout.addComponent(createToobar());
        layout.addComponent(tabSheet);
        layout.setExpandRatio(tabSheet, 1f);

        setCaption(table.getName());
    }

    private Component createToobar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");

        CheckBox checkBox = new CheckBox("Pin Tab", false);
        pinned = checkBox;

        customToolbar.setWidth("100%");

        toolbar.addComponent(checkBox);
        toolbar.addComponent(customToolbar);
        toolbar.setExpandRatio(customToolbar, 1f);

        return toolbar;
    }

    private TabSheet createTabSheet(JdbcTable table, DatabaseSession databaseSession) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addStyleName(Reindeer.TABSHEET_SMALL);

        TableStructureView tableStructureView = new TableStructureView(table, databaseSession);
        tableStructureView.setSizeFull();

        TableDataView tableContentView = new TableDataView(table, databaseSession);
        tableContentView.setSizeFull();

//        TableSourceView tableSourceView = new TableSourceView(table, sessionFactory);
//        tableSourceView.setSizeFull();

        tabSheet.addTab(tableStructureView);
        tabSheet.addTab(tableContentView);
//        tabSheet.addTab(tableSourceView);

        tabSheet.addListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                customToolbar.removeAllComponents();
                Component selectedTab = event.getTabSheet().getSelectedTab();
                if (selectedTab instanceof ToolbarContributor) {
                    Component contextToolbar = ((ToolbarContributor) selectedTab).getToolbar();
                    customToolbar.addComponent(contextToolbar);
                }
            }
        });

        return tabSheet;
    }

    @Override
    public DetailsState getDetailsState() {
        State state = new State();
        state.selectedTabIndex = tabSheet.getTabPosition(tabSheet.getTab(tabSheet.getSelectedTab()));
        return state;
    }

    @Override
    public void setDetailsState(DetailsState state) {
        if (state instanceof State) {
            TabSheet.Tab tab = tabSheet.getTab(((State) state).selectedTabIndex);
            tabSheet.setSelectedTab(tab.getComponent());
        }
    }

    @Override
    public boolean isTemporary() {
        return Boolean.FALSE.equals(pinned.getValue());
    }

    private static class State implements DetailsState {
        int selectedTabIndex;
    }
}
