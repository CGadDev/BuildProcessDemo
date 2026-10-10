# BuildProcessDemo

BuildProcessDemo is a Java application used to demonstrate automated
builds, unit testing, packaging, continuous integration, and application
containerization.

## Technologies

- Java 17
- Maven
- JUnit
- Jenkins
- Docker
- Git/GitHub

## Build

The project is built with Maven:

mvn clean package

This command compiles the application, runs the unit tests, and
packages the application as a JAR.

## Continuous Integration

The Jenkins pipeline is defined in the Jenkinsfile. Jenkins retrieves
the project from GitHub and executes the existing Maven build process.

## Containerization

The Dockerfile creates a container image using the JAR produced by the
Maven build process. The application runs on port 8081.

Build the Docker image with:

docker build -t build-process-demo:v1 .

Run the container with:

docker run -d --name build-demo-v1 -p 8081:8081 build-process-demo:v1

The application can then be accessed at:

http://localhost:8081

The status endpoint is available at:

http://localhost:8081/status

## Author

Christina Gadson  
CEN4802 - Software Integration, Configuration, and Testing

