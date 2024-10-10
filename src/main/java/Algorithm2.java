import GDB.Entity;
import com.google.common.base.Strings;
import model.Neo4JType;
import model.Node;
import model.Query;
import GDB.Schema;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import utils.ConnectDatabase;
import utils.Expression;
import utils.Utils;
import utils.cypher.CypherExpression;
import utils.cypher.CypherVisitor;
import utils.neo4j.Neo4JExpressionGenerator;

import java.util.*;

class Algorithm2 {
    static Random rand = new Random();
    /** Hàm sinh biểu thức ngẫu nhiên từ entity */
    static String generateExpression(Entity<Neo4JType> entity, Neo4JType type) {
        String result = null;
        while (result == null) {
            //Lấy danh sách các thuộc tính
            List<String> propertyKeys = entity.getPropertiesByType(type);

            if (propertyKeys.isEmpty()) {
                type = Neo4JType.getRandom();
                continue;
            }

            // Chọn một thuộc tính ngẫu nhiên từ entity
            String property = propertyKeys.get(rand.nextInt(propertyKeys.size()));

            // Chọn một phép toán ngẫu nhiên dựa trên kiểu dữ liệu.
            String operator = Utils.generateRandomOperator(type);

            //Sinh giá trị ngẫu nhiên cho vế phải của biểu thức
            String rightExpression = Utils.generateRandomValue(type);
            while (Strings.isNullOrEmpty(rightExpression)) {
                rightExpression = Utils.generateRandomValue(type);
            }

            //Nếu kiểu dữ liệu là chuỗi (Neo4JType.STRING),
            // giá trị chuỗi được bao quanh bởi dấu ' để đảm bảo đúng cú pháp Cypher.
            if (type.equals(Neo4JType.STRING)) {
                rightExpression = "'" + rightExpression + "'";
            }

            result = "n." + property + " " + operator + " " + rightExpression;
        }
        return result;
    }

    /** Thực thi truy vấn trên Neo4j và trả về kết quả */
    static Long executeQuery(Session session, String queryString) {
        var result = session.run(queryString);
        if(result == null || !result.hasNext()) return  0L;
        var count = result.single().get("count");
        return count.isNull() ? 0L : count.asLong();
    }


    /** Tạo các truy vấn phân chia điều kiện (True, False, NULL) */
    static Query[] applyPredicatePartitioning(String originalQuery, Expression predicate) {
        Query trueQuery = new Query(originalQuery + " WHERE " + predicate + " RETURN COUNT(n)"); // True
        Query falseQuery = new Query(originalQuery + " WHERE NOT " + predicate + " RETURN COUNT(n)"); // False
        Query nullQuery = new Query(originalQuery + " WHERE " + predicate + " IS NULL RETURN COUNT(n)"); // NULL

        System.out.println("True Query: " + trueQuery);
        System.out.println("False Query: " + falseQuery);
        System.out.println("Null Query: " + nullQuery);

        return new Query[]{trueQuery, falseQuery, nullQuery};
    }

    /** Kiểm tra phân chia điều kiện */
    static void checkPredicatePartitioning(Session session, String originalQuery, Query[] partitionQueries) {
        // Chạy truy vấn gốc
        Long originalResult = executeQuery(session, originalQuery);

        // Chạy ba truy vấn phân chia điều kiện
        Long trueResult = executeQuery(session, partitionQueries[0].getBaseQuery());
        Long falseResult = executeQuery(session, partitionQueries[1].getBaseQuery());
        Long nullResult = executeQuery(session, partitionQueries[2].getBaseQuery());

        // Kết hợp ba kết quả
        Long combinedResult = trueResult + falseResult + nullResult;

        // So sánh kết quả ban đầu với kết quả phân chia
        if(originalResult != combinedResult){
            originalQuery += " RETURN COUNT(n)";
            System.out.println("Bug found with query: " + originalQuery);
        }
    }

    public static String getWhereClause(Entity<Neo4JType> entity) {
        var whereCondition = Neo4JExpressionGenerator.generateExpression(Map.of("n", entity), Neo4JType.BOOLEAN);
        return CypherVisitor.asString(whereCondition);
    }

    // Hàm kiểm thử: Tạo đồ thị, sinh truy vấn, kiểm tra lỗi
    public static void runTests(Session session, Schema schema) {
        for (int i = 0; i < 10; i++) { // Thực hiện 10 lần kiểm thử
            System.out.println("Test " + (i + 1) + ":");

            try {
                /** Bước 1: Sinh truy vấn ngẫu nhiên */
                //Lấy một node ngẫu nghiên để tạo câu truy vấn ban đầu
                String label = schema.getRandomLabel();
                String queryString = String.format("MATCH (n:%s)", label);
                String originalQuery = queryString + " RETURN COUNT(n)";
                System.out.println("originalQuery: " +originalQuery);

                /** Bước 2: Sinh điều kiện ngẫu nhiên */

                Entity<Neo4JType> entity = schema.getEntityByLabel(label);
                String expressionStr = generateExpression(entity, Neo4JType.getRandom());
                while (Strings.isNullOrEmpty(expressionStr)) {
                    expressionStr = generateExpression(entity, Neo4JType.getRandom());
                }
//                String expressionStr = getWhereClause(entity);
                Expression predicate = new Expression(expressionStr);

                /**Bước 3 Tạo các truy vấn điều kiện*/
                Query[] partitionQueries = applyPredicatePartitioning(queryString, predicate);

                /** Bước 4: Thực thi kiểm tra với phương pháp phân chia điều kiện */
                checkPredicatePartitioning(session, originalQuery, partitionQueries);

                System.out.println();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Driver driver = ConnectDatabase.connectNeo4j();
        try (Session session = driver.session()) {
            // Khởi tạo Schema và chạy kiểm thử
            Schema schema = Algorithm1.fetchSchemaFromNeo4j(session);
            runTests(session, schema);
        } finally {
            driver.close();
        }
    }
}
