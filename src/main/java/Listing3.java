import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class Listing3 {
    public static void main(String[] args) {
        // Khởi tạo một đồ thị TinkerGraph
        TinkerGraph graph = TinkerGraph.open();

        /**Bước 1: Khởi tạo GraphTraversalSource từ đồ thị*/
        GraphTraversalSource g = graph.traversal();

        /** Bước 2: Tạo đỉnh "Person" cho Tom Hanks */
        g.addV("Person").property("name", "Tom Hanks").next();

        /** Bước 3: Tạo đỉnh "Movie" cho bộ phim Saving Private Ryan */
        g.addV("Movie").property("title", "Saving Private Ryan").next();

        /** Bước 3: Kết nối Tom Hanks với bộ phim qua cạnh "DIRECTED" */
        g.V().has("Person", "name", "Tom Hanks").as("person")
                .V().has("Movie", "title", "Saving Private Ryan").as("movie")
                .addE("DIRECTED").from("person").to("movie").next();

        /** Bước 4: Truy vấn và in ra tiêu đề bộ phim mà Tom Hanks làm đạo diễn */
        g.V()
                .has("Person", "name", "Tom Hanks")
                .out("DIRECTED")
                .hasLabel("Movie")
                .values("title")
                .forEachRemaining(System.out::println);
    }
}
