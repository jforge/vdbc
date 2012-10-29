package org.indp.vdbc.ui.profile.impl.fields;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import org.indp.vdbc.services.DialectSupport;
import org.indp.vdbc.ui.profile.config.AbstractFormFieldFactory;

import java.util.Collection;

/**
 *
 */
public class DialectFieldFactory extends AbstractFormFieldFactory {

    public DialectFieldFactory(String title, boolean required) {
        super(title, required);
    }

    @Override
    public Field createField() {
        Collection<String> dialectCodes = DialectSupport.getDialectCodes();
        ComboBox box = new ComboBox(getTitle(), dialectCodes);
        box.setNullSelectionAllowed(false);
        box.setInputPrompt("Select a dialect");
        box.setInvalidAllowed(false);
        box.setRequired(isRequired());
        for (String code : dialectCodes) {
            box.setItemCaption(code, DialectSupport.getDialectType(code).getTitle());
        }
        return box;
    }
}
