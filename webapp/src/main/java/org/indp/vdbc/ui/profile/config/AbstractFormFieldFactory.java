package org.indp.vdbc.ui.profile.config;

/**
*
*/
public abstract class AbstractFormFieldFactory implements FormFieldFactory {
    private String title;
    private boolean required;

    public AbstractFormFieldFactory(String title, boolean required) {
        this.title = title;
        this.required = required;
    }

    public String getTitle() {
        return title;
    }

    public boolean isRequired() {
        return required;
    }
}
