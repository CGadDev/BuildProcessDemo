
# BuildProcessDemo

## Project Overview

BuildProcessDemo is a Java web application developed for CEN4802 - Software Integration, Configuration, and Testing.

The application uses Java's built-in HTTP server and Maven for building, testing, and packaging. The project has also been used to demonstrate unit testing, continuous integration, Docker containerization, and Kubernetes deployment.

## Application Logging

This assignment adds application logging using Java's built-in java.util.logging framework.

Logging is integrated into the application's HTTP request handling to record successful operations, unsupported endpoints, and processing failures.

The application uses four logging levels:

- INFO: Records successful requests and application startup.
- WARNING: Records unsupported endpoints and invalid request parameters.
- SEVERE: Records processing failures and exception details.
- FINE: Records additional information about incoming requests.

## Logging Configuration

The logging configuration is located at:

src/main/resources/logging.properties

Logs are written to two destinations:

- Eclipse Console for viewing application activity as it occurs.
- logs directory for saving entries that can be examined later.

Log entries include timestamps, severity levels, and descriptive messages.

The file handler supports log rotation with a maximum size of approximately 1 MB per file and three retained generations.

The logging level can be changed in logging.properties to control how much information is recorded.

## Building and Running the Application

Requirements:

- Java JDK 17
- Apache Maven or Eclipse with Maven integration

To build the application, run:

    mvn clean package

Alternatively, use Eclipse's Maven Build option with the clean package goal.

To start the application using the compiled classes, run:

    java -cp target/classes com.christina.builddemo.BuildServer

The application runs on port 8081.

## Application Endpoints

The following endpoints can be used to test the application:

- http://localhost:8081/
  Displays the application homepage.

- http://localhost:8081/status
  Displays the server status.

- http://localhost:8081/greet?name=Christina
  Displays a personalized greeting.

- http://localhost:8081/unknown
  Returns HTTP 404 and generates a WARNING entry.

- http://localhost:8081/simulate-error
  Returns HTTP 500 and generates a SEVERE entry with exception details.

## Generating Log Entries

While the application is running, PowerShell can be used to generate requests.

The following command sends 120 requests to the status endpoint:

    1..120 | ForEach-Object {
        curl.exe -s -o NUL http://localhost:8081/status
    }

Each successful request generates an INFO entry.

During this assignment, 123 successful status request entries were verified.

## Examining and Filtering Logs

The generated logs can be examined using PowerShell.

To view recent log entries:

    Get-Content .\logs\buildprocess.0.log -Tail 20

To count successful status requests:

    $logs = Get-ChildItem .\logs\*.log

    (Get-Content $logs.FullName |
        Select-String "INFO Request completed: method=GET, path=/status, status=200").Count

To find warnings and severe errors:

    Get-Content .\logs\*.log |
        Select-String "WARNING|SEVERE"

To examine requests to the status endpoint:

    Get-Content .\logs\*.log |
        Select-String "path=/status" |
        Select-Object -First 10

These commands help identify specific events and patterns without manually examining every log entry.

## Modifying Logging Levels

The logging configuration can be modified to enable additional diagnostic information.

For normal informational logging, use:

    com.christina.builddemo.BuildServer.level=INFO

For more detailed logging, use:

    com.christina.builddemo.BuildServer.level=FINE

The console and file handlers must also allow FINE messages.

After modifying the configuration, rebuild and restart the application.

Enabling FINE logging records additional request details, including the HTTP method, requested path, and client address.

## Testing

The existing JUnit test suite was executed after implementing the logging changes.

Results:

- Tests executed: 8
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven build: SUCCESS

The application was also tested using successful HTTP requests, invalid endpoints, and a simulated processing failure.

## Project Files

Important project files include:

- BuildServer.java - Main Java application and logging implementation.
- BuildServerTest.java - JUnit tests.
- pom.xml - Maven build configuration.
- logging.properties - Logging configuration.
- Dockerfile - Docker container configuration.
- Jenkinsfile - Jenkins pipeline configuration.
- Kubernetes configuration files - Deployment resources.

Generated build files and application logs are excluded from version control.

## Previous Coursework

The BuildProcessDemo application was originally developed and expanded through previous CEN4802 assignments.