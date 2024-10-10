import model.Graph;
import model.Neo4JType;
import model.Query;
import GDB.Schema;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import utils.ConnectDatabase;

import java.util.Set;

public class PredicatePartitioningMain {
    public static void main(String[] args) throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        Session session = driver.session();
        Schema schema = new Schema().generateRandomSchema(Set.of(Neo4JType.values()));

        // Bước 1: Tạo siêu mô hình cho các nút và cạnh
        Algorithm1.generateGraph(session, schema, 5);

        // Bước 2: Chạy thử nghiệm với 10 lần kiểm thử trên các đồ thị và truy vấn ngẫu nhiên
        Schema schemaNeo = Algorithm1.fetchSchemaFromNeo4j(session);
        Algorithm2.runTests(session, schemaNeo);
    }
}
