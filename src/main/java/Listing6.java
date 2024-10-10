import org.neo4j.driver.*;
import utils.ConnectDatabase;
import utils.DropIndexNeo4j;

public class Listing6 {
    public static void main(String[] args) throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            // Xóa index
            DropIndexNeo4j.dropIndex(session, "L", "p");
            DropIndexNeo4j.deleteNode(session, "L", "p", "test");

            /**Bước 1: Tạo node và thực hiện truy vấn không có chỉ mục*/
            session.run("CREATE (:L {p:'test'})");

            Result resultBeforeIndex = session.run("MATCH (n:L) WHERE n.p STARTS WITH lTrim(n.p) RETURN COUNT(n) AS countBefore");
            System.out.println("Count before index: " + resultBeforeIndex.single().get("countBefore"));

            /**Bước 2: Tạo chỉ mục và truy vấn*/
            session.run("CREATE INDEX FOR (n:L) ON (n.p)");

            // Truy vấn số lượng node sau khi có chỉ mục
            Result resultAfterIndex = session.run("MATCH (n:L) WHERE n.p STARTS WITH lTrim(n.p) RETURN COUNT(n) AS countAfter");
            System.out.println("Count after index: " + resultAfterIndex.single().get("countAfter"));
        }
        driver.close();
    }
}
