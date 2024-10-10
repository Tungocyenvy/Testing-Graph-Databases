package model;

import java.util.Map;

public class Node {
    private String label;
    private Map<String, Object> properties;

    public Node(String label, Map<String, Object> properties) {
        this.label = label;
        this.properties = properties;
    }

    public String getLabel() {
        return label;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
