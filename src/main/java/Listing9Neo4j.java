import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import utils.ConnectDatabase;

public class Listing9Neo4j {
    public static void main(String[] args) throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            // Tạo dữ liệu
            session.run("CREATE (:L)");

            // Truy vấn với điều kiện WHERE chứa giá trị null
            String query = "MATCH (n:L) WHERE (null <> false) XOR true RETURN COUNT(n) AS count";
            Result result = session.run(query);

            System.out.println("Count: " + result.single().get("count"));
        }
        driver.close();
    }
}
