package model;

import java.util.Map;
import java.util.Set;

public class Metamodel {
    private Set<String> labels;
    private Map<String, Object> properties;

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
