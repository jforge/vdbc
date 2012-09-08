package org.indp.vdbc.ui.explorer;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.services.DatabaseSessionManager;
import org.indp.vdbc.ui.explorer.details.TableDetailsView;
import org.indp.vdbc.util.MetadataRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class TableSelectorComponent extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(TableSelectorComponent.class);
    private static final String VALUE_PROPERTY = "value";
    private IndexedContainer tableListContainer;
    private DetailsListener detailsListener;
    private DatabaseSessionManager sessionManager;

    public TableSelectorComponent(DatabaseSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void attach() {
        super.attach();
        setSizeFull();
        try {
            Component selectors = createSelectors();

            tableListContainer = createObjectListContainer();
            Table objectList = new Table(null, tableListContainer);
            objectList.setSizeFull();
            objectList.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            objectList.setSelectable(true);
            objectList.addListener(new ItemClickListener() {

                @Override
                public void itemClick(ItemClickEvent event) {
                    if (null == detailsListener) {
                        return;
                    }
                    JdbcTable item = (JdbcTable) event.getItemId();
                    if (null == item) {
                        return;
                    }
                    detailsListener.showDetails(createDetails(item));
                }
            });

            final TextField filter = new TextField();
            filter.setInputPrompt("filter (Alt-F to focus)");
            filter.setWidth("100%");
            filter.setStyleName(Reindeer.TEXTFIELD_SMALL);

            filter.addListener(new FieldEvents.TextChangeListener() {
                @Override
                public void textChange(FieldEvents.TextChangeEvent event) {
                    String text = event.getText();
                    tableListContainer.removeAllContainerFilters();
                    tableListContainer.addContainerFilter(VALUE_PROPERTY, text, true, false);
                }
            });

            VerticalLayout vl = new VerticalLayout();
            vl.setSizeFull();
            vl.addComponent(selectors);
            vl.addComponent(objectList);
            vl.addComponent(filter);
            vl.setExpandRatio(objectList, 1f);

            Panel root = new Panel();
            root.setSizeFull();
            root.setStyleName(Reindeer.PANEL_LIGHT);
            root.setContent(vl);

            root.addAction(new ShortcutListener("activate filter", ShortcutAction.KeyCode.F, new int[]{ShortcutAction.ModifierKey.ALT}){

                @Override
                public void handleAction(Object sender, Object target) {
                    filter.focus();
                }
            });

            setCompositionRoot(root);

        } catch (SQLException ex) {
            LOG.error("failed to create table selector view", ex);
            setCompositionRoot(new Label(ex.getMessage()));
        }
    }

    protected Component createSelectors() throws SQLException {
        FormLayout l = new FormLayout();
        l.setWidth("100%");
        l.setSpacing(false);
        l.setMargin(false);

        MetadataRetriever metadataRetriever = sessionManager.getMetadata();
        String catalogTerm = metadataRetriever.getCatalogTerm();
        catalogTerm = catalogTerm.substring(0, 1).toUpperCase() + catalogTerm.substring(1);
        List<String> catalogNames = metadataRetriever.getCatalogs();
        final ComboBox catalogs = new ComboBox(catalogTerm + ":", catalogNames);
        catalogs.setWidth("100%");
        catalogs.setNullSelectionAllowed(false);
        catalogs.setImmediate(true);
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

        ValueChangeListener valueChangeListener = new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateTableList(catalogs, schemas, tableTypes);
            }
        };

        catalogs.addListener(valueChangeListener);
        schemas.addListener(valueChangeListener);
        tableTypes.addListener(valueChangeListener);

        l.addComponent(catalogs);
        l.addComponent(schemas);
        l.addComponent(tableTypes);
        return l;
    }

    protected IndexedContainer createObjectListContainer() {
        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty(VALUE_PROPERTY, String.class, null);
        return c;
    }

    protected void updateTableList(Property catalogProperty, Property schemaProperty, Property tableTypeProperty) {
        tableListContainer.removeAllItems();

        String catalog = null, schema = null, tableType = null;
        if (null != catalogProperty.getValue())
            catalog = catalogProperty.getValue().toString();
        if (null != schemaProperty.getValue())
            schema = schemaProperty.getValue().toString();
        if (null != tableTypeProperty.getValue())
            tableType = tableTypeProperty.getValue().toString();


        // TODO what if tableType == null

        List<JdbcTable> tables;
        try {
            tables = sessionManager.getMetadata().getTables(catalog, schema, tableType);
        } catch (Exception ex) {
            getApplication().getMainWindow().showNotification("Error<br/>", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
            return;
        }

        for (JdbcTable t : tables)
            tableListContainer.addItem(t).getItemProperty(VALUE_PROPERTY).setValue(t.getName());
    }

    protected ObjectDetails createDetails(JdbcTable table) {
        TableDetailsView dv = new TableDetailsView(table, sessionManager);
        return dv;
    }

    public void setDetailsListener(DetailsListener detailsListener) {
        this.detailsListener = detailsListener;
    }
}
