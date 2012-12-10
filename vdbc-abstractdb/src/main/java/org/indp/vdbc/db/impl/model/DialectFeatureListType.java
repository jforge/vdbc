package org.indp.vdbc.db.impl.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 *
 */
public class DialectFeatureListType {
    @XmlElement(name = "feature")
    private List<DialectFeature> features;

    public List<DialectFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<DialectFeature> features) {
        this.features = features;
    }
}
