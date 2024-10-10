import org.neo4j.driver.*;
import utils.ConnectDatabase;
import utils.DropIndexNeo4j;

public class Listing1 {
    public static void main(String[] args) throws Exception {
        // Kết nối với Neo4j
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            DropIndexNeo4j.dropIndex(session, "L", "p");
            DropIndexNeo4j.deleteNode(session, "L", "p", "test");
            DropIndexNeo4j.deleteNode(session, "L", "p", "null");

            /** Bước 1: Tạo Node và index */
            session.run("CREATE (:L {p:'test'})");
            session.run("CREATE INDEX FOR (n:L) ON (n.p)");

            /**Bước 2: Truy vấn đếm số node */
            Result result1 = session.run("MATCH (n:L) RETURN COUNT(n) AS count");
            System.out.println("Total nodes: " + result1.single().get("count"));

            /**Bước 3: Truy vấn với predicate */
            // 1. Truy vấn với predicate STARTS WITH lTrim(p) điều kiện True
            Result result2 = session.run("MATCH (n:L) WHERE n.p STARTS WITH lTrim(n.p) RETURN COUNT(n) AS count");
            System.out.println("Nodes where p STARTS WITH lTrim(p): " + result2.single().get("count"));

            // 2. Truy vấn với predicate NOT STARTS WITH lTrim(p) điều kiện False
            Result result3 = session.run("MATCH (n:L) WHERE NOT (n.p STARTS WITH lTrim(n.p)) RETURN COUNT(n) AS count");
            System.out.println("Nodes where NOT(p STARTS WITH lTrim(p)): " + result3.single().get("count"));

            // 3.Truy vấn với predicate IS NULL
            Result result4 = session.run("MATCH (n:L) WHERE (n.p STARTS WITH lTrim(n.p)) IS NULL RETURN COUNT(n) AS count");
            System.out.println("Nodes where (p STARTS WITH lTrim(p)) IS NULL: " + result4.single().get("count"));
        }
        driver.close();
    }
}
