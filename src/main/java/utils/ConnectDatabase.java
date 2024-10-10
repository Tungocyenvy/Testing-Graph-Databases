package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Properties;

import com.arangodb.ArangoDB;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import redis.clients.jedis.Jedis;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ConnectDatabase {
    private static final String filePath = "./database.properties";
    private static Properties prop;

    private static Properties getProperties() {
        //return the existing properties if loaded earlier
        if (prop != null) {
            return prop;
        }
        File file = new File(filePath);

        try (InputStream istream = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(istream);
            prop = properties;
            return properties;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Driver connectNeo4j() throws Exception {
        Properties properties = getProperties();

        if (properties == null || properties.get("neo4j.uri") == null) {
            throw new Exception("Neo4j cluster not defined");
        }

        String uri = properties.get("neo4j.uri").toString();
        String userName = properties.get("neo4j.username").toString();
        String password = properties.get("neo4j.password").toString();

        return GraphDatabase.driver(uri, AuthTokens.basic(userName, password));
    }

    public static Jedis connectRedis() throws Exception {
        Properties properties = getProperties();

        if (properties == null || properties.get("redis.host") == null) {
            throw new Exception("redis cluster not defined");
        }

        String host = properties.get("redis.host").toString();
        int port = Integer.parseInt(properties.get("redis.port").toString());
        String password = properties.get("redis.password").toString();

        Jedis jedis = new Jedis(host, port);
        jedis.auth(password);
        return jedis;
    }

    public static DgraphClient connectDgraph() throws Exception {
        Properties properties = getProperties();

        if (properties == null || properties.get("dgraph.uri") == null) {
            throw new Exception("Dgraph cluster not defined");
        }

        String uri = properties.get("dgraph.uri").toString();

        // Tạo kênh kết nối gRPC
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(uri)
                .useTransportSecurity() // sử dụng SSL/TLS cho kết nối an toàn
                .build();

        // Khởi tạo mảng stub (ở đây chỉ sử dụng một stub)
        DgraphGrpc.DgraphStub blockingStub = DgraphGrpc.newStub(channel);

        // Tạo client Dgraph với mảng stub
        return new DgraphClient(blockingStub);
    }

    public static ArangoDB connectArangoDB() throws Exception {
        Properties properties = getProperties();

        if (properties == null || properties.get("arangodb.encodedCA") == null) {
            throw new Exception("Arangodb cluster not defined");
        }

        String encodedCA = properties.get("arangodb.encodedCA").toString();
        String host = properties.get("arangodb.host").toString();
        int port = Integer.parseInt(properties.get("arangodb.port").toString());
        String user = properties.get("arangodb.user").toString();
        String pass = properties.get("arangodb.pass").toString();

        InputStream is = new java.io.ByteArrayInputStream(Base64.getDecoder().decode(encodedCA));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate) cf.generateCertificate(is);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null);
        ks.setCertificateEntry("caCert", caCert);

        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return new ArangoDB.Builder()
                .useSsl(true)
                .host(host, port)
                .user(user)
                .password(pass)
                .sslContext(sslContext)
                .build();
    }
}
