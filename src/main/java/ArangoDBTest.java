import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentEntity;
import utils.ConnectDatabase;

import java.util.Scanner;

public class ArangoDBTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("--------------------------");
            System.out.println("Chọn một hàm để chạy:");
            System.out.println("1. Kiểm tra phương pháp phân vùng");
            System.out.println("2. Truy vấn khoảng cách âm");
            System.out.println("3. So sánh với giá trị NaN");
            System.out.println("4. Kiểm tra tối ưu sớm");
            System.out.println("5. Thoát");

            int choice = scanner.nextInt();
            try {
                switch (choice) {
                    case 1:
                        testLogicPP();
                        break;
                    case 2:
                        testNegativeDistance();
                        break;
                    case 3:
                        testCompareNAN();
                        break;
                    case 4:
                        testOptimization();
                        break;
                    case 5:
                        System.out.println("Thoát chương trình.");
                        scanner.close();
                        return; // Thoát khỏi vòng lặp
                    default:
                        System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại.");
                }
            } catch (Exception e) {
                System.err.println("Lỗi xảy ra: " + e.getMessage());
            }
        }
    }

    public static void testLogicPP() throws Exception {
        // Kết nối với ArangoDB
        ArangoDB arangoDB = ConnectDatabase.connectArangoDB();
        ArangoDatabase database = arangoDB.db("Predicate-Partitioning");

        // xóa collection
        String collectionName = "L";
        if (database.collection(collectionName).exists()) {
            database.collection(collectionName).drop();
        }

        CollectionEntity collectionEntity = database.createCollection(collectionName);
        System.out.println("Collection created: " + collectionEntity.getName());

        /** Bước 2: Tạo Document và index */
        // Bước 1: Tạo Node và index
        BaseDocument document1 = new BaseDocument();
        document1.addAttribute("p", "test");
        DocumentEntity createdDocument = database.collection(collectionName).insertDocument(document1);
        System.out.println("Inserted node with id: " + createdDocument.getId());



        /** Bước 3: Truy vấn đếm số node */
        String countQuery = "FOR doc IN L COLLECT WITH COUNT INTO length RETURN length";
        Long count = database.query(countQuery, Long.class).next();
        System.out.println("Total nodes: " + count);

        /** Bước 4: Truy vấn với predicate */
        // 1. Truy vấn với predicate STARTS WITH lTrim(p) điều kiện True
        String queryStartsWithTrimTrue = "FOR doc IN L FILTER LIKE(doc.p, TRIM(doc.p), true) COLLECT WITH COUNT INTO length RETURN length";
        Long countStartsWithTrimTrue = database.query(queryStartsWithTrimTrue, Long.class).next();
        System.out.println("Nodes where p LIKE(TRIM(doc.p)): " + countStartsWithTrimTrue);

        // 2. Truy vấn với predicate NOT STARTS WITH lTrim(p) điều kiện False
        String queryNotStartsWithTrim = "FOR doc IN L FILTER NOT LIKE(doc.p, TRIM(doc.p), true) COLLECT WITH COUNT INTO length RETURN length";
        Long countNotStartsWithTrim = database.query(queryNotStartsWithTrim, Long.class).next();
        System.out.println("Nodes where p NOT LIKE(TRIM(doc.p)): " + countNotStartsWithTrim);

        // 3. Truy vấn với predicate NOT STARTS WITH lTrim(p) điều kiện null
        String queryNull = "FOR doc IN L FILTER doc.p == NULL COLLECT WITH COUNT INTO length RETURN length";
        Long countNull = database.query(queryNull, Long.class).next();
        System.out.println("Nodes where p IS NULL: " + countNull);

        // Đóng kết nối
        arangoDB.shutdown();
    }

    public static void testNegativeDistance() throws Exception {
        // Kết nối với ArangoDB
        ArangoDB arangoDB = ConnectDatabase.connectArangoDB();
        ArangoDatabase database = arangoDB.db("Predicate-Partitioning");
        try {

            // xóa collection
            String collectionName = "locations";
            if (database.collection(collectionName).exists()) {
                database.collection(collectionName).drop();
            }

            /**Bước 1 tạo Collection*/
            CollectionEntity collectionEntity = database.createCollection(collectionName);
            System.out.println("Collection created: " + collectionEntity.getName());

            try {
                /** Bước 2: Thực hiện truy vấn khoảng cách âm*/
                String distanceQuery = "FOR n IN " + collectionName + " "
                        + "FILTER distance(n.p.coordinates[0], n.p.coordinates[1], 1, 1) <= -1 "
                        + "RETURN n";

                ArangoCursor<String> cursor = database.query(distanceQuery, String.class);
                if (!cursor.hasNext()) {
                    System.out.println("Không tìm thấy nút nào trong khoảng cách đã chỉ định.");
                } else {
                    while (cursor.hasNext()) {
                        System.out.println("Kết quả truy vấn khoảng cách: " + cursor.next());
                    }
                }
            } catch (Exception e) {
                // In ra lỗi nếu truy vấn không hợp lệ
                System.err.println("Lỗi khi thực thi truy vấn: " + e.getMessage());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            // Đóng kết nối
            arangoDB.shutdown();
        }
    }

    public static void testCompareNAN() throws Exception {
        // Kết nối với ArangoDB
        ArangoDB arangoDB = ConnectDatabase.connectArangoDB();
        ArangoDatabase database = arangoDB.db("Predicate-Partitioning");
        try{

            // Tạo câu truy vấn
            String[] queries = {
                    "RETURN 0.0 / 0.0 == 1", // NaN equals 1
                    "RETURN 0.0 / 0.0 != 1", // NaN not equals 1
                    "RETURN 0.0 / 0.0 <= 1", // NaN less than or equal to 1
                    "RETURN 0.0 / 0.0 >= 1"  // NaN greater than or equal to 1
            };

            // Thực hiện truy vấn từng câu và in ra kết quả
            for (String query : queries) {
                try {
                    ArangoCursor<Object> cursor = database.query(query, Object.class);
                    if (cursor.hasNext()) {
                        Object result = cursor.next();
                        System.out.println("Kết quả của '" + query + "' là " + result);
                    }
                } catch (Exception e) {
                    // Nếu có lỗi, in ra lỗi
                    System.out.println("Lỗi khi thực thi truy vấn: " + e.getMessage());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            arangoDB.shutdown();
        }
    }

    public static void testOptimization () throws Exception {
        // Kết nối với ArangoDB
        ArangoDB arangoDB = ConnectDatabase.connectArangoDB();
        ArangoDatabase database = arangoDB.db("Predicate-Partitioning");
        try{

            // Tạo câu truy vấn
            String[] queries = {
                    "RETURN (0.0 / 0.0)", // NaN equals 1
                    "RETURN 0.0 < (0.0 / 0.0)", // NaN not equals 1
                    "RETURN NOT (0.0 < (0.0 / 0.0))", // NaN less than or equal to 1
            };

            // Thực hiện truy vấn từng câu và in ra kết quả
            for (String query : queries) {
                try {
                    ArangoCursor<Object> cursor = database.query(query, Object.class);
                    if (cursor.hasNext()) {
                        Object result = cursor.next();
                        System.out.println("Kết quả của '" + query + "' là " + result);
                    }
                } catch (Exception e) {
                    // Nếu có lỗi, in ra lỗi
                    System.out.println("Lỗi khi thực thi truy vấn: " + e.getMessage());
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            arangoDB.shutdown();
        }
    }
}
