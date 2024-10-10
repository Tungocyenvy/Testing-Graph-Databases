import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import utils.ConnectDatabase;
import utils.DropIndexNeo4j;

public class Listing8Neo4j {
    public static void main(String[] args) throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            // Drop existing nodes and index if they exist
            DropIndexNeo4j.dropIndex(session, "D", "p");
            // Deleting the node with specific property
            session.run("MATCH (n:D {p: point({longitude: 1, latitude: 1})}) DELETE n");
//            session.run("MATCH (n:D {p: point({longitude: 1.001, latitude: 1.001})}) DELETE n");

            // Tạo dữ liệu (nút) với tọa độ
            session.run("CREATE INDEX FOR (n:D) ON (n.p)");
            session.run("CREATE (:D {p: point({longitude: 1, latitude: 1})})");

            // Truy vấn với so sánh khoảng cách âm
            String query = "MATCH (n:D) WHERE point.distance(point({longitude: 1, latitude: 1}), n.p) <= -1 RETURN n";
            try {
                Result result = session.run(query);

                // In kết quả nếu không có lỗi
                result.stream().forEach(record -> {
                    System.out.println(record.get("n").asNode().toString());
                });
            } catch (Neo4jException e) {
                // In ra lỗi nếu truy vấn không hợp lệ
                System.err.println("Lỗi khi thực thi truy vấn: " + e.getMessage());
            }
        }
        driver.close();
    }
}
