package org.indp.vdbc.ui.settings;

import com.vaadin.ui.*;
import org.indp.vdbc.ui.profile.ConnectionProfileManager;

import java.util.Collection;

/**
 *
 */
public class ProfileTypeSelectorDialog extends Window {

    private final SelectionListener selectionListener;

    public ProfileTypeSelectorDialog(final SelectionListener selectionListener) {
        this.selectionListener = selectionListener;

        setCaption("Select Profile Type");
        setResizable(false);
        setModal(true);
        setWidth("200px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setMargin(true);
        setContent(content);

        final ComboBox selector = createProfileSelector();
        content.addComponent(selector);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(new Button("OK", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectionListener.onFactorySelected((ConnectionProfileManager) selector.getValue());
                close();
            }
        }));
        footer.addComponent(new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        }));

        content.addComponent(footer);
    }

    private ComboBox createProfileSelector() {
        ComboBox comboBox = new ComboBox("Profile Type");
        comboBox.setNullSelectionAllowed(false);
        comboBox.setWidth("100%");

        Collection<ConnectionProfileManager> factories = ConnectionProfileManager.Lookup.getAll();
        for (ConnectionProfileManager factory : factories) {
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
        void onFactorySelected(ConnectionProfileManager factory);
    }
}
