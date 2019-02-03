import com.sun.net.httpserver.HttpServer;
import handler.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class JavaHTTPServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/download/", new Handler());
        System.out.println(server.getAddress());
        server.start();

        System.out.println("Server Started!");
        System.out.println("Listening to: " + server.getAddress().getHostName() + ":" + server.getAddress().getPort());
    }
}
