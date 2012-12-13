package org.indp.vdbc.db.impl.model;

import org.indp.vdbc.db.Dialect;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@XmlRootElement(name = "dialect")
public class DialectDefinition {

    @XmlAttribute(name = "extends")
    private String baseDialect;

    @XmlAttribute(required = true)
    private String name;

    @XmlElementWrapper(name = "example-urls")
    @XmlElement(name = "url")
    private List<String> exampleUrls;

    @XmlElementWrapper(name = "drivers")
    @XmlElement(name = "driver")
    private List<String> driver;

    @XmlElement(name = "features")
    @XmlJavaTypeAdapter(DialectFeatureMapAdapter.class)
    private Map<String, DialectFeature> features;

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(DialectDefinition.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXB context.");
        }
    }

    public static DialectDefinition read(String id) {
        InputStream in;
        try {
            in = Dialect.class.getResource(id + ".xml").openStream();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read xml expressions for dialect " + id, e);
        }
        try {
            DialectDefinition dialectDefinition = (DialectDefinition) JAXB_CONTEXT.createUnmarshaller().unmarshal(in);
            if (dialectDefinition.getBaseDialect() != null) {
                mergeFeaturesFromBase(dialectDefinition);
            }
            return dialectDefinition;
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to parse dialect definition.", e);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void mergeFeaturesFromBase(DialectDefinition dialectDefinition) {
        String baseDialect = dialectDefinition.getBaseDialect();
        Map<String, DialectFeature> ourFeatures = dialectDefinition.getFeatures();
        Map<String, DialectFeature> baseFeatures = read(baseDialect).getFeatures();
        for (String key : baseFeatures.keySet()) {
            if (!ourFeatures.containsKey(key)) {
                ourFeatures.put(key, baseFeatures.get(key));
            }
        }
    }

    public Map<String, DialectFeature> getFeatures() {
        if (features == null) {
            features = new HashMap<String, DialectFeature>();
        }
        return features;
    }

    public boolean hasFeature(String name) {
        return getFeatures().containsKey(name);
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

    public List<String> getExampleUrls() {
        return exampleUrls;
    }

    public void setExampleUrls(List<String> exampleUrls) {
        this.exampleUrls = exampleUrls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDriver() {
        return driver;
    }

    public void setDriver(List<String> driver) {
        this.driver = driver;
    }
}
