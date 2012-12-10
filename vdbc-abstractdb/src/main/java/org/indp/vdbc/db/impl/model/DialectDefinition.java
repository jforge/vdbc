package org.indp.vdbc.db.impl.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

/**
 *
 */
@XmlRootElement(name = "dialect")
public class DialectDefinition {
    @XmlAttribute(name = "extends")
    private String baseDialect;

    @XmlElement(name = "features")
    @XmlJavaTypeAdapter(DialectFeatureMapAdapter.class)
    private Map<String, DialectFeature> features;

    public Map<String, DialectFeature> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, DialectFeature> features) {
        this.features = features;
    }

    public String getBaseDialect() {
        return baseDialect;
    }

    public void setBaseDialect(String baseDialect) {
        this.baseDialect = baseDialect;
    }
}
