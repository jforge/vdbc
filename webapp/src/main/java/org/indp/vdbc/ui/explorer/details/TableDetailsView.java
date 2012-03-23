package org.indp.vdbc.ui.explorer.details;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.explorer.DetailsState;
import org.indp.vdbc.ui.explorer.ObjectDetails;

/**
 *
 *
 */
public class TableDetailsView extends CustomComponent implements ObjectDetails {

    private Property pinned;
    private final HorizontalLayout customToolbar = new HorizontalLayout();

    public TableDetailsView(JdbcTable table, DatabaseSessionManager sessionManager) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);

        TabSheet tabSheet = createTabSheet(table, sessionManager);

        layout.addComponent(createToobar());
        layout.addComponent(tabSheet);
        layout.setExpandRatio(tabSheet, 1f);

        setCaption(table.getName());
    }

    private Component createToobar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");

        CheckBox checkBox = new CheckBox("Pin", false);
        pinned = checkBox;

        customToolbar.setWidth("100%");

        toolbar.addComponent(checkBox);
        toolbar.addComponent(customToolbar);
        toolbar.setExpandRatio(customToolbar, 1f);

        return toolbar;
    }

    private TabSheet createTabSheet(JdbcTable table, DatabaseSessionManager sessionManager) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addStyleName(Reindeer.TABSHEET_SMALL);

        TableStructureView tableStructureView = new TableStructureView(table, sessionManager);
        tableStructureView.setSizeFull();

        TableDataView tableContentView = new TableDataView(table, sessionManager);
        tableContentView.setSizeFull();

//        TableSourceView tableSourceView = new TableSourceView(table, sessionManager);
//        tableSourceView.setSizeFull();

        tabSheet.addTab(tableStructureView);
        tabSheet.addTab(tableContentView);
//        tabSheet.addTab(tableSourceView);

        tabSheet.addListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                customToolbar.removeAllComponents();
                Component selectedTab = event.getTabSheet().getSelectedTab();
                if (selectedTab instanceof ToolbarOwner) {
                    Component contextToolbar = ((ToolbarOwner) selectedTab).getToolbar();
                    customToolbar.addComponent(contextToolbar);
                }
            }
        });

        return tabSheet;
    }

    @Override
    public DetailsState getDetailsState() {
        return null;
    }

    @Override
    public boolean isTemporary() {
        return Boolean.FALSE.equals(pinned.getValue());
    }
}
