package model;

import java.util.Map;

public class Edge {
    private String label;
    private Map<String, Object> properties;
    private Node from;
    private Node to;

    public Edge(String label, Map<String, Object> properties, Node from, Node to) {
        this.label = label;
        this.properties = properties;
        this.from = from;
        this.to = to;
    }

    public String getLabel() {
        return label;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }
}
