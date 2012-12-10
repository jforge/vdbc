package org.indp.vdbc.db.impl.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 */
public class DialectFeature {
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String expression;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
