import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import com.redislabs.redisgraph.impl.graph_cache.RedisGraphCaches;

public class Listing8 {
    public static void main(String[] args) {
        // Kết nối đến Redis
        String graphName = "location";
        RedisGraph graph = new RedisGraph();

        /**Bước 1 Tạo chỉ mục cho nút*/
        String createIndexQuery = "CREATE INDEX FOR (n:L) ON (n.p)";
        graph.query(graphName, createIndexQuery);

        /**Bước 2 Thực hiện truy vấn với khoảng cách âm */
        String distanceQuery = "MATCH (n:L) WHERE distance(point({longitude:1,latitude:1}), n.p) <= -1 RETURN n";
        ResultSet distanceResult = graph.query(graphName, distanceQuery);
        /** Bước 3: Xử lý kết quả trả về */
        if (!distanceResult.hasNext()) {
            System.out.println("Not Found Distance");
        } else {
            while (distanceResult.hasNext()) {
                System.out.println("Distance Query Result: " + distanceResult.next().getValue("n"));
            }
        }

        graph.close();
    }
}
