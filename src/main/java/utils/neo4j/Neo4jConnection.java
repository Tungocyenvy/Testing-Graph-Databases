package utils.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;

public class Neo4jConnection {
    private final Driver driver;

    // Khởi tạo kết nối
    public Neo4jConnection(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    // Đóng kết nối
    public void close() {
        driver.close();
    }

    // Thực thi câu lệnh Cypher
    public void runQuery(String query) {
        try (Session session = driver.session()) {
            Result result = session.run(query);
            result.stream().forEach(record -> System.out.println(record));
        }
    }
}
