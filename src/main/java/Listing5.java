import org.neo4j.driver.*;
import utils.ConnectDatabase;

public class Listing5 {
    public static void main(String[] args) throws Exception {
        // Kết nối với Neo4j
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            //Thực hiện truy vấn so sánh NaN value
            String query = "RETURN (0.0/0.0) AS NaNValue," +
                                    " 0.0 < (0.0/0.0) AS NaNComparison, " +
                                    "NOT(0.0 < (0.0/0.0)) AS NotNaNComparison";
            Result result = session.run(query);

            //In ra kết quả truy vấn
            result.stream().forEach(record -> {
                System.out.println("(0.0/0.0): " + record.get("NaNValue"));
                System.out.println("So sánh (0.0 < NaN): " + record.get("NaNComparison"));
                System.out.println("So sánh NOT(0.0 < NaN): " + record.get("NotNaNComparison"));
            });
        }
        driver.close();
    }
}
