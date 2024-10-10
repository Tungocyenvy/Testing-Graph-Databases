package utils;

public class Expression {
    String expression; // Biểu thức truy vấn dạng chuỗi

    public Expression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return expression;
    }
}
