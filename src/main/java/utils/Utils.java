package utils;

import model.Neo4JType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Utils {
    static Random rand = new Random();
    public static String readAllText(String path) throws IOException {

        String content = "";

        try {
            content = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public static boolean fileExists(String path) {

        File tempFile = new File(path);
        return tempFile.exists();

    }

    /** Hàm sinh giá trị ngẫu nhiên dựa trên kiểu dữ liệu (Neo4JType) */
    public static String generateRandomValue(Neo4JType type) {
        switch (type) {
            case INTEGER:
                // Giá trị số nguyên ngẫu nhiên từ 0 đến 99
                return String.valueOf(rand.nextInt(100));

            case FLOAT:
                // Giá trị số thực ngẫu nhiên trong khoảng từ 0.0 đến 99.9
                return String.format("%.2f", rand.nextFloat() * 100);

            case BOOLEAN:
                // Giá trị boolean ngẫu nhiên
                return String.valueOf(rand.nextBoolean());

            case DURATION:
                // Tạo một độ dài ngẫu nhiên (ví dụ: "P3Y6M4DT12H30M5S" cho 3 năm, 6 tháng, 4 ngày, 12 giờ, 30 phút, 5 giây)
                return String.format("P%dY%dM%dDT%dH%dM%dS", rand.nextInt(10), rand.nextInt(12), rand.nextInt(31), rand.nextInt(24), rand.nextInt(60), rand.nextInt(60));

            case DATE:
                // Sinh ngày ngẫu nhiên trong khoảng từ năm 2000 đến năm 2023
                int year = 2000 + rand.nextInt(24); // Năm từ 2000 đến 2023
                int month = 1 + rand.nextInt(12); // Tháng từ 1 đến 12
                int day = 1 + rand.nextInt(28); // Ngày từ 1 đến 28 (để tránh ngày không hợp lệ)
                return String.format("%04d-%02d-%02d", year, month, day); // Định dạng ngày là YYYY-MM-DD

            case LOCAL_TIME:
                // Sinh thời gian địa phương ngẫu nhiên (ví dụ: "12:34:56")
                return String.format("%02d:%02d:%02d", rand.nextInt(24), rand.nextInt(60), rand.nextInt(60));

            case POINT:
                // Tạo một điểm ngẫu nhiên (ví dụ: "POINT({longitude: 12.34, latitude: 56.78})")
                double longitude = -180 + rand.nextDouble() * 360; // Giới hạn từ -180 đến 180
                double latitude = -90 + rand.nextDouble() * 180; // Giới hạn từ -90 đến 90
                return String.format("POINT({longitude: %.6f, latitude: %.6f})", longitude, latitude);

            default:
                return Randomization.getString(); // Giá trị mặc định
        }
    }

    public static String generateRandomOperator(Neo4JType type) {
        switch (type) {
            case INTEGER:
            case FLOAT:
            case DATE:
            case LOCAL_TIME:
                // Các phép toán so sánh cho số nguyên và số thực
                String[] numericOperators = {"<", ">", "=", "<=", ">=", "!="};
                return numericOperators[rand.nextInt(numericOperators.length)];
            case BOOLEAN:
                // Các phép toán cho boolean
                return rand.nextBoolean() ? "IS TRUE" : "IS FALSE";
            default:
                return "="; // Phép toán mặc định
        }
    }


}
