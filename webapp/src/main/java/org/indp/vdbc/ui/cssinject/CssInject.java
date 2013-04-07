package org.indp.vdbc.ui.cssinject;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.Component;

import java.util.UUID;

/**
 *
 */
@JavaScript("css-inject.js")
public class CssInject extends AbstractJavaScriptExtension {

    private String elementId;

    @Override
    public void extend(AbstractClientConnector target) {
        if (!(target instanceof Component)) {
            throw new IllegalArgumentException("target is not Component");
        }
        String id = ((Component) target).getId();
        if (id == null) {
            id = "vdbc-" + UUID.randomUUID().toString();
            ((Component) target).setId(id);
        }
        elementId = id;

        super.extend(target);
    }

    public void addStyle(String css) {
        // todo remove elementId param (this.getElement() in js should return it?)
        callFunction("addStyle", elementId, css);
    }
}
