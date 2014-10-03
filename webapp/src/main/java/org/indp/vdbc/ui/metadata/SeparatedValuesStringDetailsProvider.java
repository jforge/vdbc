package org.indp.vdbc.ui.metadata;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.indp.vdbc.ui.UiUtils;

public abstract class SeparatedValuesStringDetailsProvider implements DetailsProvider {

    private static final String VALUE_PROPERTY = "value";
    private final String separatorRegex;

    public SeparatedValuesStringDetailsProvider(String separatorRegex) {
        this.separatorRegex = separatorRegex;
    }

    public SeparatedValuesStringDetailsProvider() {
        this(",");
    }

    protected abstract String getValueString() throws Exception;

    @Override
    public Component getDetailsComponent() {
        String value;
        try {
            value = getValueString();
        } catch (Exception ex) {
            return new Label(ex.getMessage());
        }

        String[] values = value.split(separatorRegex);
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(VALUE_PROPERTY, String.class, null);
        for (int i = 0; i < values.length; ++i) {
            Item item = container.addItem(i);
            item.getItemProperty(VALUE_PROPERTY).setValue(values[i].trim());
        }

        Table t = UiUtils.createTable(container);
        t.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        return t;
    }
}
