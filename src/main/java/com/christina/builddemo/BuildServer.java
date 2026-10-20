// Christina Gadson
// 10/09/2026


package com.christina.builddemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

public class BuildServer {

    public static final int PORT = 8081;

    private static final Logger LOGGER =
            Logger.getLogger(BuildServer.class.getName());

    // Returns the webpage content for the requested path.
    static String getResponseBody(String path) {
        if ("/".equals(path)) {
            return "<html><body>"
                    + "<h1>Build Process Demo</h1>"
                    + "<p>This application was packaged using an automated Maven build "
                    + "and verified through Jenkins CI.</p>"
                    + "<p>This updated version is now running in a Docker container.</p>"
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

    // Returns a personalized greeting using a name from the URL.
    static String getResponseBody(String path, String query) {
        if ("/greet".equals(path)) {
            String name = "Guest";

            if (query != null) {
                for (String parameter : query.split("&")) {
                    String[] parts = parameter.split("=", 2);

                    if (parts.length == 2 && "name".equals(parts[0])) {
                        try {
                            String enteredName = URLDecoder.decode(
                                    parts[1], StandardCharsets.UTF_8).trim();

                            if (!enteredName.isEmpty()) {
                                name = enteredName;
                            } else {
                                LOGGER.warning(
                                        "Empty greeting name; using default.");
                            }
                        } catch (IllegalArgumentException e) {
                            LOGGER.log(Level.WARNING,
                                    "Invalid greeting parameter; using default.",
                                    e);
                        }
                        break;
                    }
                }
            }

            String safeName = name.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");

            return "<html><body>"
                    + "<h1>Hello, " + safeName + "!</h1>"
                    + "<p>Welcome to BuildProcessDemo.</p>"
                    + "</body></html>";
        }

        return getResponseBody(path);
    }

    // Returns the HTTP status code for a requested path.
    static int getStatusCode(String path) {
        if ("/".equals(path)
                || "/status".equals(path)
                || "/greet".equals(path)) {
            return 200;
        }

        return 404;
    }

    // Loads logging configuration from the Maven resources folder.
    private static void configureLogging() throws IOException {
        Files.createDirectories(Path.of("logs"));

        try (InputStream config = BuildServer.class
                .getResourceAsStream("/logging.properties")) {

            if (config == null) {
                throw new IOException(
                        "Logging configuration file was not found.");
            }

            LogManager.getLogManager().readConfiguration(config);
        }
    }

    public static void main(String[] args) throws IOException {
        configureLogging();

        HttpServer server =
                HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getRawQuery();
            String method = exchange.getRequestMethod();

            LOGGER.fine("Received request: method=" + method
                    + ", path=" + path
                    + ", client=" + exchange.getRemoteAddress());

            String response;
            int statusCode;

            try {
                if ("/simulate-error".equals(path)) {
                    throw new IllegalStateException(
                            "Simulated processing failure for logging demonstration.");
                }

                response = getResponseBody(path, query);
                statusCode = getStatusCode(path);

                if (statusCode == 404) {
                    LOGGER.warning(
                            "Unsupported endpoint: method=" + method
                            + ", path=" + path
                            + ", status=404");
                } else {
                    LOGGER.info(
                            "Request completed: method=" + method
                            + ", path=" + path
                            + ", status=" + statusCode);
                }

            } catch (Exception e) {
                statusCode = 500;
                response = "<html><body>"
                        + "<h1>500 - Internal Server Error</h1>"
                        + "</body></html>";

                LOGGER.log(Level.SEVERE,
                        "Request processing failed: method=" + method
                        + ", path=" + path
                        + ", status=500", e);
            }

            byte[] responseBytes =
                    response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type", "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(
                    statusCode, responseBytes.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(responseBytes);
            }
        });

        server.setExecutor(null);
        server.start();

        LOGGER.info("BuildProcessDemo started on port " + PORT);
    }
}
