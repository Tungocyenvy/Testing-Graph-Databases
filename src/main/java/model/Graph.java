package model;

import java.util.HashSet;
import java.util.Set;

public class Graph {
    private Set<Node> nodes;
    private Set<Edge> edges;

    public Graph(){
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public Graph(Set<Node> nodes, Set<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }
    // Thêm một nút vào đồ thị
    public void addNode(Node node) {
        nodes.add(node);
    }

    // Thêm một cạnh vào đồ thị
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    // Thực hiện truy vấn giả lập và trả về kết quả
    public Set<Node> executeQuery(Query query) {
        // Giả lập thực hiện truy vấn và trả về kết quả, ở đây ta chỉ trả về tất cả các nút
        Set<Node> result = new HashSet<>(nodes);
        // Lý tưởng là bạn cần xử lý câu truy vấn và lọc dữ liệu theo điều kiện
        return result;
    }

    @Override
    public String toString() {
        return "Nodes: " + nodes + "\nEdges: " + edges;
    }

}
