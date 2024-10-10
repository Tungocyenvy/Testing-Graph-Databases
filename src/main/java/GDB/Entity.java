package GDB;


import model.Neo4JType;
import utils.Randomization;
import utils.cypher.CypherUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Lớp đại diện cho một model.Entity (model.Node hoặc Relationship)
public class Entity<T> {

    public Entity(){
        this.availableProperties = new HashMap<>();
    }

    private final Map<String, T> availableProperties;

    public Entity(Map<String, T> availableProperties) {
        this.availableProperties = availableProperties;
    }
    /**
     * Generates an entity based on a set of available types.
     * The property names are enforced to be unique by consulting takenNames.
     */
    public static <E> Entity<E> generateRandomEntity(Set<E> availableTypes, Set<String> takenNames) {
        Map<String, E> availableProperties = new HashMap<>();

        for (int i = 0; i < Randomization.nextInt(1, 6); i++) {
            String name = Randomization.generateUniqueElement(takenNames, CypherUtil::generateValidName);
            takenNames.add(name);

            availableProperties.put(name, Randomization.fromSet(availableTypes));
        }

        return new Entity<>(availableProperties);
    }

    public Map<String, T> getAvailableProperties() {
        return availableProperties;
    }

    // Phương thức mới để lấy các thuộc tính kiểu boolean
    public List<String> getPropertiesByType(Neo4JType type) {
        return availableProperties.entrySet().stream()
                .filter(entry -> entry.getValue() == type) // Kiểm tra kiểu
                .map(Map.Entry::getKey) // Lấy tên thuộc tính
                .collect(Collectors.toList());
    }

}