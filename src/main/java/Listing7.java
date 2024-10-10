import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import utils.ConnectDatabase;

public class Listing7 {
    public static void main(String[] args) throws Exception {
        Jedis redis = ConnectDatabase.connectRedis();
        String[] queries = {
                "return 0.0/0.0 == 1", // NaN equals 1
                "return 0.0/0.0 ~= 1", // NaN not equals 1
                "return 0.0/0.0 <= 1", // NaN less than or equal to 1
                "return 0.0/0.0 >= 1"  // NaN greater than or equal to 1
        };

        //Thực hiên truy vấn từng câu và in ra kết quả
        for (String query : queries) {
            try {
                Object result = redis.eval(query);
                System.out.println("Result of '" + query.replace("return ","") + "' is " + result);
            }catch (JedisDataException e){
                // Nếu có lỗi như phép toán không hợp lệ, in ra lỗi
                System.out.println("Error running script: " + e.getMessage());
            }
        }
        redis.close();
    }


}