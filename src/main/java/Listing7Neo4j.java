import org.neo4j.driver.*;
import utils.ConnectDatabase;

public class Listing7Neo4j {
    public static void main(String[] args) throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            String[] queries = {
                    "RETURN 0.0/0.0 = 1 AS NaNEqualsOne",
                    "RETURN 0.0/0.0 <> 1 AS NaNNotEqualsOne",
                    "RETURN 0.0/0.0 <= 1 AS NaNLessEqualOne",
                    "RETURN 0.0/0.0 >= 1 AS NaNGreaterEqualOne"
            };

            for (String query : queries) {
                Result result = session.run(query);
                System.out.println(result.single().asMap());
            }
        }
        driver.close();
    }


}
