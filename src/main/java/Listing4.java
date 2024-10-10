import model.Entity;
import model.Schema;

public class Listing4 {
    public static void main(String[] args) {
        // Tạo một schema đơn giản cho node và edge
        Schema schema = new Schema();

        // Tạo model.Entity cho node và thêm thuộc tính
        Entity personEntity = new Entity();
        personEntity.addProperty("name", "String");
        personEntity.addProperty("age", "Integer");
        schema.nodeSchema.put("Person", personEntity);

        // Tạo model.Entity cho relationship và thêm thuộc tính
        Entity relationshipEntity = new Entity();
        relationshipEntity.addProperty("since", "Date");
        schema.relationshipSchema.put("FRIEND", relationshipEntity);

        // In schema đã tạo
        System.out.println("model.Node schema for Person: " + schema.nodeSchema.get("Person").getAvailableProperties());
        System.out.println("Relationship schema for FRIEND: " + schema.relationshipSchema.get("FRIEND").getAvailableProperties());
    }
}
