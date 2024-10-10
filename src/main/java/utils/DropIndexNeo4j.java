package utils;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.Map;

public class DropIndexNeo4j {
    public static void dropIndex(Session session, String label, String prop) {
        // Retrieve all indexes
        Result result = session.run("SHOW INDEXES");
        String indexName = null;

        while (result.hasNext()) {
            Record record = result.next();
            String entityType = record.get("entityType").asString(); // Index entity type (NODE or RELATIONSHIP)

            // Null check before calling asList() on labelsOrTypes and properties
            List<Object> labelsOrTypesList = record.get("labelsOrTypes").isNull() ? null : record.get("labelsOrTypes").asList();
            List<Object> propertiesList = record.get("properties").isNull() ? null : record.get("properties").asList();

            // If labelsOrTypes or properties is null, skip this record
            if (labelsOrTypesList == null || propertiesList == null) {
                continue;
            }

            // Convert lists of labels/types and properties to strings
            String labelsOrTypes = String.join(", ", labelsOrTypesList.stream().map(Object::toString).toArray(String[]::new));
            String properties = String.join(", ", propertiesList.stream().map(Object::toString).toArray(String[]::new));

            // Check if the index matches the label `L` and property `p`
            if (entityType.equals("NODE") && labelsOrTypes.contains(label) && properties.contains(prop)) {
                indexName = record.get("name").asString(); // Get the index name
                break;
            }
        }

        if (indexName != null) {
            System.out.println("Found index: " + indexName);

            // Drop the existing index
            String dropQuery = "DROP INDEX " + indexName + " IF EXISTS";
            session.run(dropQuery);
            System.out.println("Index dropped.");
        }
    }

    public static void deleteNode(Session session, String label, String propertyKey, String propertyValue) {
        // Using a transaction to delete the node
        String cypherQuery = "MATCH (n:" + label + " {" + propertyKey + ": $value}) DETACH DELETE n";
        try (Transaction tx = session.beginTransaction()) {
            tx.run(cypherQuery, Map.of("value", propertyValue)); // Replace with your property value
            tx.commit(); // Commit the transaction
        }
    }

    public static void deleteMultiNode(Session session, String label, String propertyKey, String propertyValue) {
        // Using a transaction to delete the node
         String cypherQuery = "MATCH (n:" + label + ") where " + "n." + propertyKey + " IN [" + propertyValue + "] DETACH DELETE n";
        try (Transaction tx = session.beginTransaction()) {
            tx.run(cypherQuery); // Replace with your property value
            tx.commit(); // Commit the transaction
        }
    }

    public static void deleteAllNeo4j(Session session){
        String cypherQuery = "MATCH (n) DETACH DELETE n";
        try (Transaction tx = session.beginTransaction()) {
            tx.run(cypherQuery); // Replace with your property value
            tx.commit(); // Commit the transaction
        }
    }
}
