package org.indp.vdbc.ui.settings;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.VerticalLayout;
import org.indp.vdbc.ui.profile.ConnectionProfileSupportService;

import java.util.Collection;

public class ProfileTypeSelectorDialog extends Window {

    public ProfileTypeSelectorDialog(final SelectionListener selectionListener) {
        setCaption("Select Profile Type");
        setResizable(false);
        setModal(true);
        setWidth("250px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);

        final ComboBox selector = createProfileSelector();
        content.addComponent(selector);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(new Button("OK", (Button.ClickEvent event) -> {
            selectionListener.onFactorySelected((ConnectionProfileSupportService) selector.getValue());
            close();
        }));
        footer.addComponent(new Button("Cancel",
                (Button.ClickEvent event) -> {
                    close();
                }));

        content.addComponent(footer);
        content.setComponentAlignment(footer, Alignment.MIDDLE_CENTER);
    }

    private ComboBox createProfileSelector() {
        ComboBox comboBox = new ComboBox("Profile Type");
        comboBox.setNullSelectionAllowed(false);
        comboBox.setTextInputAllowed(false);
        comboBox.setWidth("100%");

        Collection<ConnectionProfileSupportService> factories = ConnectionProfileSupportService.Lookup.getAll();
        for (ConnectionProfileSupportService factory : factories) {
            comboBox.addItem(factory);
            comboBox.setItemCaption(factory, factory.getName());
        }
        if (!factories.isEmpty()) {
            comboBox.select(factories.iterator().next());
        }
        return comboBox;
    }

    public interface SelectionListener {
        /**
         * Called after OK button was pressed.
         *
         * @param factory selected factory
         */
        void onFactorySelected(ConnectionProfileSupportService factory);
    }
}
