package model;

import java.util.HashMap;
import java.util.Map;

// Lớp đại diện cho model.Schema của các node và relationship
public class Schema {
    public Map<String, Entity> nodeSchema = new HashMap<>();
    public Map<String, Entity> relationshipSchema = new HashMap<>();
}