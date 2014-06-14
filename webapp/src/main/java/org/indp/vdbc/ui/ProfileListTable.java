package org.indp.vdbc.ui;

import com.google.common.base.Strings;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.indp.vdbc.SettingsManager;
import org.indp.vdbc.model.config.ConnectionProfile;

import java.util.List;

public class ProfileListTable extends Table {

    public ProfileListTable(Property.ValueChangeListener valueChangeListener) {
        List<ConnectionProfile> profileList = SettingsManager.get().getConfiguration().getProfiles();
        setSizeFull();
        setImmediate(true);
        setSelectable(true);
        setNullSelectionAllowed(false);
        setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);

        addGeneratedColumn("color", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                String color = ((ConnectionProfile) itemId).getColor();
                return Strings.isNullOrEmpty(color)
                        ? null
                        : new Label("<div style=\"width: 10px; height: 10px; background-color: #" + color + "\"></div>", ContentMode.HTML);
            }
        });

        addContainerProperty("title", String.class, "");
        setItemCaptionPropertyId("title");
        setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);

        setColumnWidth("color", 12);

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
