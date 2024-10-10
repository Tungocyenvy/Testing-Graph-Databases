import model.*;
import GDB.Entity;
import GDB.Schema;
import org.neo4j.driver.*;
import utils.ConnectDatabase;
import utils.Randomization;
import utils.Utils;

import java.util.*;

public class Algorithm1 {

    static Random rand = new Random();

    /** Tạo các thuộc tính ngẫu nhiên cho nút hoặc cạnh */
    static Map<String, Object> generateProperties(Entity<Neo4JType> entity) {
        Map<String, Object> properties = new HashMap<>();
        for (Map.Entry<String, Neo4JType> entry : entity.getAvailableProperties().entrySet()) {
            if (rand.nextBoolean()) {  // 50% xác suất chọn thuộc tính
                properties.put(entry.getKey(), Utils.generateRandomValue(entry.getValue()));
            }
        }
        return properties;
    }


    /** Sinh một cạnh ngẫu nhiên giữa hai nút */
    static Edge generateEdge(GDB.Schema schema, Node source, Node target) {
        String type = schema.getRandomType(); /// Lấy ngẫu nhiên một nhãn cho cạnh
        GDB.Entity entity = schema.getEntityByType(type);
        return new Edge(type, generateProperties(entity), source, target); // Tạo cạnh
    }

    /** Sinh một nút ngẫu nhiên dựa trên schema*/
    static Node generateNode(GDB.Schema schema) {
        String label = schema.getRandomLabel(); // Chọn ngẫu nhiên một nhãn
        if (label == null || label.isEmpty()) {
            return null; // Trả về null nếu không có nhãn
        }

        GDB.Entity entity = schema.getEntityByLabel(label);
        if (entity == null) {
            return null; // Trả về null nếu không có entity
        }

        Map<String, Object> properties = generateProperties(entity);
        if (properties == null || properties.isEmpty()) {
            return null;
        }

        return new Node(label, properties); // Tạo nút với nhãn và thuộc tính
    }

    /**Tạo đồ thị*/
    static Graph generateGraph(Session session, Schema schema, int numberOfNodes) {
        Graph graph = new Graph();
        List<Node> nodes = new ArrayList<>();

        // Tạo một số lượng nút cố định
        while (nodes.size() < numberOfNodes) {
            Node node = generateNode(schema);
            if (node == null) {
                continue; // Bỏ qua nếu nút bị null
            }
            nodes.add(node);
            graph.addNode(node);
        }
        // Tạo các cạnh ngẫu nhiên giữa các nút
        for (Node source : nodes) {
            for (Node target : nodes) {
                // Tạo cạnh ngẫu nhiên giữa hai nút khác nhau
                if (!source.equals(target) && rand.nextBoolean()) {
                    Edge edge = generateEdge(schema, source, target); // Sinh cạnh ngẫu nhiên
                    if (edge.getFrom() == null || edge.getTo() == null) {
                        continue; // Bỏ qua cạnh nếu có nút bị null
                    }
                    graph.addEdge(edge);
                }
            }
        }

        // Thêm nút và cạnh vào Neo4j
        addNodesToNeo4j(session, graph);
        addEdgesToNeo4j(session, graph);

        return graph;
    }

    /** Thêm nút vào Neo4j */
    static void addNodesToNeo4j(Session session, Graph graph) {
        for (Node node : graph.getNodes()) {
            StringBuilder query = new StringBuilder("CREATE (n:" + node.getLabel() + " {");
            Map<String, Object> properties = node.getProperties();

            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                query.append(entry.getKey()).append(": '").append(entry.getValue()).append("', ");
            }

            if (!properties.isEmpty()) {
                query.delete(query.length() - 2, query.length()); // Xóa dấu phẩy cuối cùng
            }
            query.append("})");

            session.writeTransaction(tx -> tx.run(query.toString()));
        }
    }

    /** Lấy thuộc tính ngẫu nhiên từ danh sách thuộc tính của nút, đảm bảo không trả về null */
    static String getRandomAttribute(Map<String, Object> properties) {
        if (properties == null || properties.isEmpty()) {
            return "default"; // Trả về thuộc tính mặc định nếu không có thuộc tính
        }

        List<String> keys = new ArrayList<>(properties.keySet());
        // Kiểm tra danh sách keys có rỗng không và chọn ngẫu nhiên một thuộc tính
        if (!keys.isEmpty()) {
            return keys.get(rand.nextInt(keys.size())); // Chọn ngẫu nhiên một thuộc tính
        } else {
            return "default"; // Trả về thuộc tính mặc định nếu không có thuộc tính nào
        }
    }

    // Thêm cạnh vào Neo4j
    static void addEdgesToNeo4j(Session session, Graph graph) {
        for (Edge edge : graph.getEdges()) {
            Node fromNode = edge.getFrom();
            Node toNode = edge.getTo();

            // Kiểm tra xem nút nguồn và nút đích có null không
            if (fromNode == null || toNode == null) {
                continue; // Bỏ qua cạnh nếu một trong hai nút là null
            }

            String fromLabel = fromNode.getLabel();
            String toLabel = toNode.getLabel();

            // Chọn thuộc tính ngẫu nhiên từ các thuộc tính của nút nguồn và nút đích
            String fromAttribute = getRandomAttribute(fromNode.getProperties());
            String fromValue = fromNode.getProperties().get(fromAttribute).toString();

            String toAttribute = getRandomAttribute(toNode.getProperties());
            String toValue = toNode.getProperties().get(toAttribute).toString();

            // Kiểm tra xem nhãn hoặc thuộc tính có null không
            if (fromLabel == null || toLabel == null || fromAttribute == null || toAttribute == null) {
                continue; // Bỏ qua nếu nhãn hoặc thuộc tính bị thiếu
            }

            // Tạo câu truy vấn MATCH cho các nút nguồn và đích
            StringBuilder query = new StringBuilder("MATCH (n1:" + fromLabel + " {" + fromAttribute + ": '" + fromValue + "'}), ");
            query.append("(n2:" + toLabel + " {" + toAttribute + ": '" + toValue + "'}) ");

            // Tạo câu truy vấn CREATE cho cạnh
            query.append("CREATE (n1)-[r:" + edge.getLabel() + " {");

            // Duyệt qua các thuộc tính của cạnh
            Map<String, Object> properties = edge.getProperties();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                query.append(entry.getKey()).append(": '").append(entry.getValue()).append("', ");
            }

            // Xóa dấu phẩy cuối cùng và đóng dấu ngoặc
            if (!properties.isEmpty()) {
                query.delete(query.length() - 2, query.length()); // Xóa dấu phẩy cuối cùng
            }
            query.append("}]->(n2)");

            // Thực thi truy vấn
            session.writeTransaction(tx -> tx.run(query.toString()));
        }
    }


    /** Phương thức để lấy schema từ Neo4j */
    public static Schema fetchSchemaFromNeo4j(Session session) {
        Schema schema = new Schema();

        // Lấy tất cả các nhãn từ cơ sở dữ liệu
        String query = "CALL db.labels()"; // Lấy danh sách các nhãn
        List<String> labels = session.run(query).list(record -> record.get(0).asString());

        // Lặp qua từng nhãn để lấy thuộc tính
        for (String label : labels) {
            String propertiesQuery = "MATCH (n:" + label + ") RETURN keys(n) AS properties LIMIT 1"; // Lấy thuộc tính của nhãn
            var result = session.run(propertiesQuery);

            // Kiểm tra xem có bản ghi nào không
            if (result.hasNext()) {
                List<String> properties = result.single().get("properties").asList(Value::asString);

                Entity<Neo4JType> entity = new Entity<>();
                for (String property : properties) {
                    entity.getAvailableProperties().put(property, Neo4JType.STRING); // Giả định thuộc tính là STRING, bạn có thể thay đổi theo nhu cầu
                }
                schema.getNodeSchema().put(label, entity);
            } else {
                System.out.println("No properties found for label: " + label);
            }
        }

        return schema;
    }


    public static void main(String[] args) throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        /**Bước 1 Tạo schema ngẫu nhiên dựa trên các kiểu dữ liệu của Neo4j */
        //nguồn: https://github.com/gdbmeter/gdbmeter
        Schema schema = new Schema().generateRandomSchema(Set.of(Neo4JType.values()));

        try (Session session = driver.session()) {
            /**Bước 2 Tạo đồ thị với 5 nút */
            Graph graph = generateGraph(session, schema, 5);

            //in ra các cạnh và các nút vừa tạo
            System.out.println("Nodes:");
            graph.getNodes().forEach(node -> {
                System.out.println("model.Node: " + node.getLabel() + ", Properties: " + node.getProperties());
            });

            System.out.println("\nEdges:");
            graph.getEdges().forEach(edge -> {
                System.out.println("model.Edge: " + edge.getLabel() + ", Properties: " + edge.getProperties() +
                        ", From: " + edge.getFrom().getLabel() + ", To: " + edge.getTo().getLabel());
            });
        }

        driver.close();
    }
}
