import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import utils.ConnectDatabase;
import utils.DropIndexNeo4j;

public class DeleteAllNode {

    public static void deleteNeo4j() throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        Session session = driver.session();
        DropIndexNeo4j.deleteAllNeo4j(session);
        System.out.println("Delete Neo4j Successful!");
    }

    public static void main(String[] args) throws Exception {
        deleteNeo4j();
    }

}
