import com.redislabs.redisgraph.Record;
import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.impl.api.RedisGraph;

public class Listing9 {
    public static void main(String[] args) throws Exception {
        RedisGraph graph = new RedisGraph();
        String graphName = "exampleGraph";
        try {
            /**Bước 1  Tạo ra một nút mới với label là L mà không có bất kỳ thuộc tính nào*/
            String createDataQuery = "CREATE (:L)";
            graph.query(graphName, createDataQuery);

            /**Bước 2 Thực hiện truy vấn */
            String query = "MATCH (n:L) WHERE (null <> false) XOR true RETURN COUNT(n) AS count";
            ResultSet resultSet = graph.query(graphName, query);

            /**Bước 3 hiển thị kết quả truy vấn*/
            while (resultSet.hasNext()) {
                Record record = resultSet.next();
                System.out.println("Count: " + record.getValue("count"));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        // Đóng kết nối RedisGraph
        graph.close();
    }
}
