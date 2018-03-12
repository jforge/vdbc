package org.indp.vdbc.ui;

import com.google.common.base.Strings;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;

import java.util.List;

public class ProfileListTable extends Table {

    public ProfileListTable(Property.ValueChangeListener valueChangeListener) {
        List<ConnectionProfile> profileList = SettingsManager.get().getConfiguration().getProfiles();
        setSizeFull();
        setImmediate(true);
        setSelectable(true);
        addStyleName(ValoTheme.TABLE_COMPACT);
        addStyleName(ValoTheme.TABLE_BORDERLESS);
        addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        addStyleName(ValoTheme.TABLE_NO_STRIPES);
        setNullSelectionAllowed(false);
        setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);

        addGeneratedColumn("color", (Table source, Object itemId, Object columnId) -> {
            String color = ((ConnectionProfile) itemId).getColor();
            return Strings.isNullOrEmpty(color)
                    ? null
                    : new Label("<div style=\"width: 10px; height: 10px; border: 1px solid gray; background-color: #" + color + "\"></div>", ContentMode.HTML);
        });

        addContainerProperty("title", String.class, "");
        setItemCaptionPropertyId("title");
        setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);

        setColumnWidth("color", 20);

        for (ConnectionProfile profile : profileList) {
            Item item = addItem(profile);
            item.getItemProperty("title").setValue(profile.getName());
        }

        addValueChangeListener(valueChangeListener);

        if (!profileList.isEmpty()) {
            select(profileList.get(0));
        }
    }
}
