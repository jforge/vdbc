package org.indp.vdbc.ui.explorer;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.Notification;
import org.indp.vdbc.DatabaseSessionManager;
import org.indp.vdbc.model.jdbc.JdbcTable;
import org.indp.vdbc.ui.explorer.details.TableDetailsView;
import org.indp.vdbc.util.MetadataRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class TableSelectorView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(TableSelectorView.class);
    private static final String VALUE_PROPERTY = "value";
    private IndexedContainer tableListContainer;
    private DetailsListener detailsListener;
    private DatabaseSessionManager sessionManager;

    public TableSelectorView(DatabaseSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        try {
            setSizeFull();

            VerticalLayout vl = new VerticalLayout();
            vl.setSizeFull();
            Component selectors = createSelectors();

            tableListContainer = createObjectListContainer();
            Table objectList = new Table(null, tableListContainer);
            objectList.setSizeFull();
            objectList.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            objectList.setSelectable(true);
            objectList.addListener(new ItemClickListener() {

                @Override
                public void itemClick(ItemClickEvent event) {
                    if (null == detailsListener)
                        return;

                    JdbcTable item = (JdbcTable) event.getItemId();
                    if (null == item) {
                        return;
                    }
                    detailsListener.showDetails(createDetails(item));
                }
            });

            vl.addComponent(selectors);

            vl.addComponent(objectList);
            vl.setExpandRatio(objectList, 1f);

            addComponent(vl);

        } catch (SQLException ex) {
            LOG.error("failed to create table selector view", ex);
        }
    }

    protected Component createSelectors() throws SQLException {
        FormLayout l = new FormLayout();
        l.setWidth("100%");
        l.setSpacing(false);
//        l.setMargin(true, false, true, false);
        l.setMargin(false);

        MetadataRetriever metadataRetriever = sessionManager.getMetadata();
        String catalogTerm = metadataRetriever.getCatalogTerm();
        catalogTerm = catalogTerm.substring(0, 1).toUpperCase() + catalogTerm.substring(1, catalogTerm.length());
        List<String> catalogNames = metadataRetriever.getCatalogs();
        final ComboBox catalogs = new ComboBox(catalogTerm + ":", catalogNames);
        catalogs.setWidth("100%");
        catalogs.setNullSelectionAllowed(false);
        catalogs.setImmediate(true);
        catalogs.setVisible(!catalogNames.isEmpty());

        String schemaTerm = metadataRetriever.getSchemaTerm().toLowerCase();
        schemaTerm = schemaTerm.substring(0, 1).toUpperCase() + schemaTerm.substring(1, schemaTerm.length());

        List<String> schemaNames = metadataRetriever.getSchemas();
        final ComboBox schemas = new ComboBox(schemaTerm + ":", schemaNames);
        schemas.setWidth("100%");
        schemas.setNullSelectionAllowed(false);
        schemas.setImmediate(true);
        schemas.setVisible(!schemaNames.isEmpty());

        List<String> tableTypesList = metadataRetriever.getTableTypes();
        final ComboBox tableTypes = new ComboBox("Table type:", tableTypesList);
        if (tableTypesList.contains("TABLE"))
            tableTypes.select("TABLE");
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

//        TextField filter = new TextField("Filter:");
//        filter.setWidth("100%");

        l.addComponent(catalogs);
        l.addComponent(schemas);
        l.addComponent(tableTypes);
//        l.addComponent(filter);
        return l;
    }

    protected IndexedContainer createObjectListContainer() {
        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty(VALUE_PROPERTY, String.class, null);
        return c;
    }

    protected void updateTableList(Property catalogProperty, Property schemaProperty, Property tableTypeProperty) {
        tableListContainer.removeAllItems();
//        if (null == schemaProperty.getValue() || null == tableTypeProperty.getValue())
//            return;

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

    protected Component createDetails(JdbcTable table) {
        TableDetailsView dv = new TableDetailsView(table, sessionManager);
        return dv;
    }

    public void setDetailsListener(DetailsListener detailsListener) {
        this.detailsListener = detailsListener;
    }
}
