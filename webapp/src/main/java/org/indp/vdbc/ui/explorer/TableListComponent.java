package org.indp.vdbc.ui.explorer;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.explorer.details.TableDetailsView;
import org.indp.vdbc.util.MetadataRetriever;

import java.sql.SQLException;
import java.util.List;

public class TableListComponent extends VerticalLayout {

    private static final String VALUE_PROPERTY = "value";
    private IndexedContainer tableListContainer;
    private DetailsListener detailsListener;
    private final DatabaseSession databaseSession;

    public TableListComponent(DatabaseSession databaseSession) throws SQLException {
        this.databaseSession = databaseSession;

        Component selectors = createSelectors();

        tableListContainer = createObjectListContainer();
        Table objectList = new Table(null, tableListContainer);
        objectList.setSizeFull();
        objectList.addStyleName(ValoTheme.TABLE_COMPACT);
        objectList.addStyleName(ValoTheme.TABLE_BORDERLESS);
        objectList.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        objectList.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        objectList.setSelectable(true);
        objectList.setNullSelectionAllowed(false);
        objectList.addItemClickListener((ItemClickEvent event) -> {
            if (null == detailsListener) {
                return;
            }
            JdbcTable item = (JdbcTable) event.getItemId();
            if (null == item) {
                return;
            }
            detailsListener.showDetails(createDetails(item));
        });

        final TextField filter = new TextField();
        filter.setInputPrompt("filter");
        filter.setWidth("100%");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);


        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                String text = event.getText();
                tableListContainer.removeAllContainerFilters();
                tableListContainer.addContainerFilter(VALUE_PROPERTY, text, true, false);
            }
        });

        setSizeFull();
        addComponents(selectors, objectList, filter);
        setExpandRatio(objectList, 1f);
    }

    protected Component createSelectors() throws SQLException {
        FormLayout form = new FormLayout();
        form.setSpacing(false);
        form.setMargin(false);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        MetadataRetriever metadataRetriever = databaseSession.getMetadata();
        String catalogTerm = metadataRetriever.getCatalogTerm();
        catalogTerm = catalogTerm.substring(0, 1).toUpperCase() + catalogTerm.substring(1);
        List<String> catalogNames = metadataRetriever.getCatalogs();
        final ComboBox catalogs = new ComboBox(catalogTerm + ":", catalogNames);
        catalogs.setWidth("100%");
        catalogs.setNullSelectionAllowed(false);
        catalogs.setImmediate(true);
        catalogs.setFilteringMode(FilteringMode.CONTAINS);
        catalogs.setVisible(!catalogNames.isEmpty());
        if (catalogNames.size() == 1) {
            catalogs.select(catalogNames.get(0));
        }

        String schemaTerm = metadataRetriever.getSchemaTerm().toLowerCase();
        schemaTerm = schemaTerm.substring(0, 1).toUpperCase() + schemaTerm.substring(1);

        List<String> schemaNames = metadataRetriever.getSchemas();
        final ComboBox schemas = new ComboBox(schemaTerm + ":", schemaNames);
        schemas.setWidth("100%");
        schemas.setNullSelectionAllowed(false);
        schemas.setImmediate(true);
        schemas.setFilteringMode(FilteringMode.CONTAINS);
        schemas.setInputPrompt("<none>");
        schemas.setVisible(!schemaNames.isEmpty());
        if (schemaNames.size() == 1) {
            schemas.select(schemaNames.get(0));
        }

        List<String> tableTypesList = metadataRetriever.getTableTypes();
        final ComboBox tableTypes = new ComboBox("Table type:", tableTypesList);
        if (tableTypesList.contains("TABLE")) {
            tableTypes.select("TABLE");
        }
        tableTypes.setWidth("100%");
        tableTypes.setNullSelectionAllowed(false);
        tableTypes.setImmediate(true);
        tableTypes.setFilteringMode(FilteringMode.CONTAINS);

        ValueChangeListener valueChangeListener = (ValueChangeEvent event) -> {
            updateTableList(catalogs, schemas, tableTypes);
        };

        catalogs.addValueChangeListener(valueChangeListener);
        schemas.addValueChangeListener(valueChangeListener);
        tableTypes.addValueChangeListener(valueChangeListener);

        form.addComponent(catalogs);
        form.addComponent(schemas);
        form.addComponent(tableTypes);
        return form;
    }

    protected IndexedContainer createObjectListContainer() {
        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty(VALUE_PROPERTY, String.class, null);
        return c;
    }

    protected void updateTableList(Property catalogProperty, Property schemaProperty, Property tableTypeProperty) {
        tableListContainer.removeAllItems();

        String catalog = null, schema = null, tableType = null;
        if (null != catalogProperty.getValue()) {
            catalog = catalogProperty.getValue().toString();
        }
        if (null != schemaProperty.getValue()) {
            schema = schemaProperty.getValue().toString();
        }
        if (null != tableTypeProperty.getValue()) {
            tableType = tableTypeProperty.getValue().toString();
        }

        // TODO what if tableType == null

        List<JdbcTable> tables;
        try {
            tables = databaseSession.getMetadata().getTables(catalog, schema, tableType);
        } catch (Exception ex) {
            Notification.show("Error\n", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            return;
        }

        for (JdbcTable t : tables) {
            tableListContainer.addItem(t).getItemProperty(VALUE_PROPERTY).setValue(t.getName());
        }
    }

    protected ObjectDetails createDetails(JdbcTable table) {
        return new TableDetailsView(table, databaseSession);
    }

    public void setDetailsListener(DetailsListener detailsListener) {
        this.detailsListener = detailsListener;
    }
}
