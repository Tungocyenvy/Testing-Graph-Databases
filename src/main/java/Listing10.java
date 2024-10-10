import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

public class Listing10 {
    public static void main(String[] args) {
        JanusGraph graph = JanusGraphFactory.open("inmemory");
        GraphTraversalSource g = graph.traversal();

        /** Bước 1 Tạo schema và dữ liệu */
        graph.tx().open();
        graph.makeVertexLabel("L").make();
        graph.makePropertyKey("p").dataType(Integer.class).make();
        graph.makePropertyKey("q").dataType(Integer.class).make();
        graph.tx().commit();

        /**Bước 2 Tạo đỉnh mới với nhãn L mà không có thuộc tính q*/
        g.addV("L").property("p", 1).iterate();

        /**Bước 3 Truy vấn với thuộc tính q không tồn tại*/
        System.out.println("Count (has q): " + g.V().hasLabel("L").has("q").count().next());
        System.out.println("Count (has not q): " + g.V().hasLabel("L").has("q", P.neq(2)).count().next());

        graph.close();
    }
}
