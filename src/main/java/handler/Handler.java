package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import download.DependencyDownloader;

import java.io.*;

public class Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Trying to handle request...");

        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/octec-stream");
        headers.add("Content-Disposition","attachment;filename=plugin.jar");

        exchange.sendResponseHeaders(200, 0);
        try (BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())) {
            System.out.println(exchange.getRequestURI().toString().replace("/api/download/", ""));
            File file = new DependencyDownloader().getResource("plugin", exchange.getRequestURI().toString().replace("/api/download/", ""));
            if(file == null){
                System.out.println("The file was null!");
                return;
            }

            try (InputStream inputStream = new FileInputStream(file)) {
                byte [] buffer = new byte [inputStream.available()];
                int count;
                while ((count = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
            }
        }

        exchange.getResponseBody().close();
    }
}
