package model;


import org.apache.tinkerpop.gremlin.structure.T;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Lớp đại diện cho một model.Entity (model.Node hoặc Relationship)
public class Entity {
    public Map<String, Object> availableProperties = new HashMap<>(); // Thuộc tính và kiểu dữ liệu của chúng

    public void addProperty(String name, Object type) {
        availableProperties.put(name, type);
    }

    public Set<String> getAvailableProperties() {
        return availableProperties.keySet(); // Returns a set of property names
    }
}