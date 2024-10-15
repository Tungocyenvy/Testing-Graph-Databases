import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.core.schema.JanusGraphManagement;

public class Listing10 {
    public static void main(String[] args) {
        // Mở JanusGraph trong bộ nhớ
        JanusGraph graph = JanusGraphFactory.open("inmemory");
        GraphTraversalSource g = graph.traversal();
        JanusGraphManagement management = graph.openManagement();

        /** Bước 1: Tạo schema */
        VertexLabel vertexLabel = management.makeVertexLabel("L").make();
        PropertyKey pKey = management.makePropertyKey("p").dataType(Integer.class).make();
        PropertyKey qKey = management.makePropertyKey("q").dataType(Integer.class).make();

        /** Tạo chỉ mục hỗn hợp (Mixed Index) nếu có backend */
        management.buildIndex("mixedIndex", Vertex.class)
                .addKey(pKey)
                .addKey(qKey)
                .indexOnly(vertexLabel)
                .buildCompositeIndex();

        management.commit();

        /** Bước 2: Tạo đỉnh mới với nhãn "L" mà không có thuộc tính "q" */
        g.addV("L").property("p", 1).iterate();

        /** Bước 3: Truy vấn với thuộc tính "q" không tồn tại */
        System.out.println("Count (has q): " + g.V().hasLabel("L").has("q").count().next()); // Kết quả mong đợi: 0
        System.out.println("Count (has not q): " + g.V().hasLabel("L").has("q", P.neq(2)).count().next()); // Kết quả mong đợi: 1

        graph.close();
    }
}
