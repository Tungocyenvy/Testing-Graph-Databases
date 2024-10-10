package model;

public class Query {
    String queryString; // Truy vấn dưới dạng chuỗi ký tự

    public Query(String queryString) {
        this.queryString = queryString;
    }

    // Trả về chuỗi truy vấn
    public String getBaseQuery() {
        return queryString;
    }

    @Override
    public String toString() {
        return queryString;
    }
}
