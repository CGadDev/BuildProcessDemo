// Christina Gadson
// 9/10/2026

package com.christina.builddemo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;

public class BuildServer {

    public static final int PORT = 8081;

    // Returns the webpage content for the requested path.
    static String getResponseBody(String path) {

        if ("/".equals(path)) {
            return "<html><body>"
                    + "<h1>Build Process Demo</h1>"
                    + "<p>This application was packaged using "
                    + "an automated Maven build.</p>"
                    + "</body></html>";
        }

        if ("/status".equals(path)) {
            return "<html><body>"
                    + "<h1>Status</h1>"
                    + "<p>Server is running.</p>"
                    + "</body></html>";
        }

        return "<html><body>"
                + "<h1>404 - Not Found</h1>"
                + "</body></html>";
    }

    // Returns the HTTP status code for the requested path.
    static int getStatusCode(String path) {

        if ("/".equals(path) || "/status".equals(path)) {
            return 200;
        }

        return 404; 
    }

    public static void main(String[] args) throws IOException {

        HttpServer server =
                HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", exchange -> {

            String path = exchange.getRequestURI().getPath();

            String response = getResponseBody(path);
            int statusCode = getStatusCode(path);

            byte[] responseBytes =
                    response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(
                    statusCode,
                    responseBytes.length);

            try (OutputStream output =
                    exchange.getResponseBody()) {

                output.write(responseBytes);
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println(
                "Build Process Demo server started on port "
                + PORT + ".");
    }
}