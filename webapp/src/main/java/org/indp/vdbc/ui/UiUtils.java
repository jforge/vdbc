package org.indp.vdbc.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class UiUtils {

    public static Component createHorizontalSpacer(int width) {
        Label l = new Label();
        l.setWidth(width, Sizeable.Unit.PIXELS);
        return l;
    }

    public static Table createTable(Container container) {
        Table table = new Table(null, container) {
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
                Object value = property.getValue();
                if (value == null) {
                    return "(null)";
                }

                Class<?> propertyClass = property.getType();
                Formatter formatter = FORMATTERS.get(propertyClass);
                return formatter == null ? value.toString() : formatter.format(value);
            }
        };

        for (Object propertyId : container.getContainerPropertyIds()) {
            Class<?> type = container.getType(propertyId);
            if (Number.class.isAssignableFrom(type)) {
                table.setColumnAlignment(propertyId, Table.Align.RIGHT);
            }
        }

        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.setPageLength(100); // todo configure
        table.setSelectable(true);
        table.setSortEnabled(false);
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setNullSelectionAllowed(false);
        table.setSizeFull();

        return table;
    }

    private static final Map<Class, Formatter> FORMATTERS = new HashMap<>();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:SSS").toFormatter();

    static {
        Formatter<Date> dateFormatter = new Formatter<Date>() {
            @Override
            public String format(Date value) {
                return DATE_TIME_FORMATTER.print(value.getTime());
            }
        };
        Formatter<Timestamp> timestampFormatter = new Formatter<Timestamp>() {
            @Override
            public String format(Timestamp value) {
                return TIMESTAMP_FORMATTER.print(value.getTime());
            }
        };

        FORMATTERS.put(Date.class, dateFormatter);
        FORMATTERS.put(java.sql.Date.class, dateFormatter);
        FORMATTERS.put(Timestamp.class, timestampFormatter);
    }

    private interface Formatter<T> {

        String format(T value);
    }

    private UiUtils() {
    }
}
