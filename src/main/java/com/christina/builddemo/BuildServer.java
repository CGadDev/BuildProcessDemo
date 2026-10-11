// Christina Gadson
// 10/09/2026

package com.christina.builddemo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;

public class BuildServer {

    public static final int PORT = 8081;

    // Returns the webpage content for the requested path.
    static String getResponseBody(String path) {

        // Main page displays information about the automated build process.
        if ("/".equals(path)) {
            return "<html><body>"
                    + "<h1>Build Process Demo</h1>"
                    + "<p>This application was packaged using an automated Maven build "
                    + "and verified through Jenkins CI.</p>"
                    + "<p>This updated version is now running in a Docker container.</p>"
                    + "</body></html>";
        }

        // Status page confirms that the server is running.
        if ("/status".equals(path)) {
            return "<html><body>"
                    + "<h1>Status</h1>"
                    + "<p>Server is running.</p>"
                    + "</body></html>";
        }

        // Any unsupported path returns a simple 404 page.
        return "<html><body>"
                + "<h1>404 - Not Found</h1>"
                + "</body></html>";
    }

    // Returns a personalized greeting using a name from the URL.
    static String getResponseBody(String path, String query) {

        if ("/greet".equals(path)) {

            // Use a default name if none is provided.
            String name = "Guest";

            // Search the URL query for a name parameter.
            if (query != null) {
                for (String parameter : query.split("&")) {
                    String[] parts = parameter.split("=", 2);

                    if (parts.length == 2 && "name".equals(parts[0])) {

                        try {
                            String enteredName = URLDecoder.decode(
                                    parts[1], StandardCharsets.UTF_8).trim();

                            if (!enteredName.isEmpty()) {
                                name = enteredName;
                            }
                        } catch (IllegalArgumentException e) {
                            // Use the default name for invalid URL encoding.
                            name = "Guest";
                        }

                        break;
                    }
                }
            }

            // Escape HTML characters in user-provided names.
            String safeName = name.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");

            // Generate the personalized greeting webpage.
            return "<html><body>"
                    + "<h1>Hello, " + safeName + "!</h1>"
                    + "<p>Welcome to BuildProcessDemo.</p>"
                    + "</body></html>";
        }

        // Preserve the existing homepage and status responses.
        return getResponseBody(path);
    }

    // Returns the appropriate HTTP status code for the requested path.
    static int getStatusCode(String path) {

        if ("/".equals(path) ||
                "/status".equals(path) ||
                "/greet".equals(path)) {
            return 200;
        }

        return 404;
    }

    public static void main(String[] args) throws IOException {

        // Creates the HTTP server on the configured port.
        HttpServer server =
                HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", exchange -> {

            // Retrieve the requested path and query parameters.
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getRawQuery();

            // Generate the response and corresponding status code.
            String response = getResponseBody(path, query);
            int statusCode = getStatusCode(path);

            byte[] responseBytes =
                    response.getBytes(StandardCharsets.UTF_8);

            // Configure the HTTP response content type.
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
