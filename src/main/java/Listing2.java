import org.neo4j.driver.*;
import utils.ConnectDatabase;
import utils.DropIndexNeo4j;

public class Listing2 {
    public static void main(String[] args) throws Exception {
        // Kết nối với Neo4j
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            DropIndexNeo4j.deleteNode(session,"person", "name", "Tom Hanks");
            DropIndexNeo4j.deleteMultiNode(session,"Movie", "title", "'Saving Private Ryan', 'Saving Private Ryan'");

            /** Bước 1: Tạo dữ liệu */
            //Tạo một node với nhãn Person và thuộc tính name bằng "Tom Hanks".
            //Tạo một node với nhãn Movie và thuộc tính title bằng "Saving Private Ryan".
            //Câu lệnh cũng tạo ra một mối quan hệ giữa hai node này, được biểu diễn bằng mũi tên -[:DIRECTED]->.
            // Mối quan hệ này có nhãn DIRECTED và cho biết Tom Hanks đạo diễn bộ phim "Saving Private Ryan".
            session.run("CREATE (:Person {name:'Tom Hanks'})-[:DIRECTED]->(:Movie {title:'Saving Private Ryan'})");

            /**Bước 2: Truy vấn tìm tất cả các phim được đạo diễn bởi Tom Hanks */
            String query = "MATCH (:Person {name: 'Tom Hanks'})-[:DIRECTED]->(movie:Movie) RETURN movie.title AS title";
            Result result = session.run(query);
            //in ra màn hình tiêu đề các bộ phim từ kết quả truy vấn
            result.stream().forEach(record -> {
                System.out.println("Movie directed by Tom Hanks: " + record.get("title"));
            });
        }
        driver.close();
    }
}
