package org.indp.vdbc.ui.metadata;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 *
 *
 */
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
        Table t = new Table();
        t.setSizeFull();
        t.setSelectable(true);
        t.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        t.addContainerProperty(VALUE_PROPERTY, String.class, null);
//        t.setColumnHeader(VALUE_PROPERTY, title);
        for (int i = 0; i < values.length; ++i) {
            Item item = t.addItem(i);
            item.getItemProperty(VALUE_PROPERTY).setValue(values[i].trim());
        }
        return t;
    }
}
