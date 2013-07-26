package org.indp.vdbc.db.impl.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DialectFeatureMapAdapter extends XmlAdapter<DialectFeatureListType, Map<String, DialectFeature>> {
    @Override
    public Map<String, DialectFeature> unmarshal(DialectFeatureListType v) throws Exception {
        if (v != null && v.getFeatures() != null && !v.getFeatures().isEmpty()) {
            Map<String, DialectFeature> map = new HashMap<>(v.getFeatures().size());
            for (DialectFeature feature : v.getFeatures()) {
                map.put(feature.getId(), feature);
            }
            return map;
        }
        return null;
    }

    @Override
    public DialectFeatureListType marshal(Map<String, DialectFeature> v) throws Exception {
        if (v != null && !v.isEmpty()) {
            DialectFeatureListType r = new DialectFeatureListType();
            r.setFeatures(new ArrayList<>(v.values()));
            return r;
        }
        return null;
    }
}
