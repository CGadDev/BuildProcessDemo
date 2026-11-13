# BuildProcessDemo

## Project Overview

BuildProcessDemo is a Java web application developed for CEN4802 - Software Integration, Configuration, and Testing.

The application uses Java's built-in HTTP server and Maven for building, testing, and packaging. The project has also been used to demonstrate unit testing, continuous integration, Docker containerization, Kubernetes deployment, application logging, and Infrastructure as Code.

## Application Logging

The application includes logging using Java's built-in java.util.logging framework.

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

The following endpoints are defined in the application source code:

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

The available endpoints in a Docker deployment depend on the application version packaged into the image.

## Generating Log Entries

While the application is running, PowerShell can be used to generate requests.

The following command sends 120 requests to the status endpoint:

    1..120 | ForEach-Object {
        curl.exe -s -o NUL http://localhost:8081/status
    }

Each successful request generates an INFO entry.

During the logging assignment, 123 successful status request entries were verified.

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

## Infrastructure as Code

### Overview

The Infrastructure as Code assignment extends the existing BuildProcessDemo deployment workflow by introducing Terraform to provision and manage Docker infrastructure through declarative configuration files.

Previously, the application's deployment environment was managed through Docker commands and Jenkins automation. Terraform provides an additional approach that allows infrastructure resources to be defined, validated, created, modified, and removed through a consistent configuration.

The implementation uses Terraform with Docker Desktop to manage a dedicated network and application container without requiring a cloud account or paid infrastructure services.

### IaC Environment

The implementation was completed using:

- Terraform v1.17.1
- Docker Desktop
- Docker Engine v29.8.2
- Kreuzwerker Docker Provider v4.6.0
- Windows PowerShell
- Java 17
- Apache Maven

The Terraform configuration is stored in:

terraform/main.tf

Terraform connects to Docker Desktop through the Windows Docker Engine named pipe.

### Infrastructure Configuration

The Terraform configuration defines two managed resources:

- Docker Network: A dedicated bridge network named build-demo-iac-network, used to provide network connectivity for the application container.
- Docker Container: A container named build-demo-iac, used to run the existing BuildProcessDemo Java application.

The configuration also references the existing build-process-demo:v2 Docker image through a Terraform data source. The image itself is not created or managed by Terraform.

The initial infrastructure configuration used the following settings:

- Container name: build-demo-iac
- Docker network: build-demo-iac-network
- Docker image: build-process-demo:v2
- Internal port: 8081
- External port: 8090
- Memory allocation: 512 MiB

The final configuration uses:

- Internal port: 8081
- External port: 8095
- Memory allocation: 768 MiB

### Preparing the Application

Before using Terraform, Docker Desktop must be running with Linux containers enabled.

From the BuildProcessDemo project directory, the application can be built using Maven:

    mvn clean package

The Docker image can then be built using the existing Dockerfile:

    docker build -t build-process-demo:v2 .

The Terraform configuration references this image when provisioning the application container.

### Terraform Initialization and Validation

Navigate to the Terraform directory:

    cd terraform

Initialize the Terraform working directory:

    terraform init

This command initializes the configuration and downloads the required Docker provider.

Format and validate the configuration:

    terraform fmt

    terraform validate

During implementation, Terraform initialization completed successfully and the configuration passed validation.

### Infrastructure Planning

Before provisioning the infrastructure, generate an execution plan:

    terraform plan

Terraform compares the declarative configuration with the current infrastructure state and identifies the resources that must be created, modified, or removed.

The initial execution plan proposed creating two resources:

- One Docker application container.
- One dedicated Docker bridge network.

The initial plan reported:

Plan: 2 to add, 0 to change, 0 to destroy.

This confirmed that Terraform was prepared to provision the required resources without modifying or removing unrelated infrastructure.

### Infrastructure Provisioning

After reviewing the execution plan, provision the resources using:

    terraform apply

Enter yes when prompted to confirm the operation.

The initial provisioning completed successfully and reported:

Apply complete! Resources: 2 added, 0 changed, 0 destroyed.

Terraform created the dedicated Docker network and application container using the existing BuildProcessDemo image.

The resulting infrastructure was inspected using:

    terraform state list

    docker ps --filter "name=build-demo-iac"

    docker network ls --filter "name=build-demo-iac-network"

The initial application was successfully accessed through:

http://localhost:8090

The application homepage loaded correctly, confirming that the Terraform-managed container supported the execution of the Java web application.

### Infrastructure Modification

To demonstrate infrastructure modification, the Terraform configuration was updated to change the container's memory allocation and external port.

The original configuration used:

- External port: 8090
- Memory allocation: 512 MiB

The modified configuration increased the memory allocation to 768 MiB and initially attempted to use external port 8091.

After updating the configuration, the following commands were executed:

    terraform fmt

    terraform validate

    terraform plan

Terraform identified that the existing container required replacement to apply the updated port configuration. The dedicated Docker network did not require replacement.

The modification plan reported:

Plan: 1 to add, 0 to change, 1 to destroy.

During the initial modification attempt, Docker encountered an error because port 8091 was already occupied by another process. Terraform removed the previous container, but the replacement could not start.

The conflicting process was investigated using PowerShell commands, including:

    netstat -ano | findstr ":8091"

    Get-Process -Id 10412

The process was identified as erl.exe, an Erlang runtime process.

Rather than terminating an unrelated process, the Terraform configuration was updated to use external port 8095.

The revised configuration was validated and reviewed before running:

    terraform apply

Terraform successfully replaced the incomplete container while retaining the existing Docker network.

The completed operation reported:

Apply complete! Resources: 1 added, 0 changed, 1 destroyed.

### Infrastructure Modification Verification

After applying the modified configuration, the container was verified using:

    docker ps --filter "name=build-demo-iac"

The output confirmed that the replacement container was running and that external port 8095 was mapped to internal port 8081.

The memory allocation was verified using:

    docker inspect build-demo-iac --format '{{.HostConfig.Memory}}'

The command returned:

805306368

This confirmed that the container was configured with 768 MiB of memory.

Terraform's state was also examined using:

    terraform state list

The state confirmed that Terraform continued to track the application container and dedicated Docker network.

Finally, the application homepage was accessed through:

http://localhost:8095

The homepage loaded successfully, confirming that the updated infrastructure supported the intended application environment.

### Infrastructure Removal

After verifying the modified infrastructure, the managed resources were removed using Terraform.

A destruction plan was generated using:

    terraform plan -destroy

The plan reported:

Plan: 0 to add, 0 to change, 2 to destroy.

This indicated that Terraform planned to remove the application container and dedicated Docker network.

The removal operation was performed using:

    terraform destroy

After entering yes to confirm the operation, Terraform successfully removed both resources.

The operation reported:

Destroy complete! Resources: 2 destroyed.

### Infrastructure Removal Verification

After the removal operation, Terraform's remaining state was checked using:

    terraform state list

The command returned no managed resources.

Docker was also checked using:

    docker ps -a --filter "name=build-demo-iac"

    docker network ls --filter "name=build-demo-iac-network"

Both commands returned empty resource listings, confirming that the application container and dedicated network had been removed.

The existing Docker image, application source files, and unrelated Docker infrastructure were not removed.

### Implementation Analysis

Implementing Infrastructure as Code helped me better understand how application infrastructure can be managed through declarative configuration rather than relying entirely on manually executed Docker commands. My previous workflow used Maven, Jenkins, and Docker to build, test, and deploy the application. Terraform extended this workflow by allowing me to define the required infrastructure resources and manage their lifecycle through configuration files.

One of the main advantages was the ability to generate execution plans before applying changes. During initial provisioning, Terraform identified the two resources that needed to be created. During the modification stage, it recognized that changing the container's published port required replacing the container while keeping the existing network intact. This made it easier to understand what changes would occur before executing them.

The most significant challenge involved a port conflict during the infrastructure modification. Although the Terraform configuration passed validation, Docker could not start the replacement container because another process was already using port 8091. I investigated the conflict, selected another available port, and reapplied the configuration. This demonstrated that successful configuration validation does not necessarily guarantee that the deployment environment is free of runtime conflicts.

Another observation was that Terraform could manage the container replacement automatically after the initial modification failed. Once the configuration was corrected, Terraform identified the incomplete container and successfully replaced it without requiring manual deletion of the managed resources.

Overall, Terraform provided a more structured and repeatable method of managing the application's infrastructure. A potential improvement would be introducing input variables for settings such as the external port, memory allocation, and container name. This would make the configuration easier to reuse across different environments without directly editing the main configuration file.

### Terraform Project Files

The Infrastructure as Code implementation includes the following files:

- terraform/main.tf - Defines the Docker provider, image reference, network, container, and application URL output.
- terraform/.terraform.lock.hcl - Records the selected provider dependency version.
- .gitignore - Excludes generated Terraform state, provider downloads, temporary files, and other build artifacts.

Terraform also generates local state files during execution. These files are excluded from version control because they contain environment-specific infrastructure information.

The final main.tf retains the modified infrastructure configuration using port 8095 and a 768 MiB memory limit.

To reproduce the original modification sequence, the initial configuration must first be deployed using port 8090 and 512 MiB of memory. The configuration can then be updated to port 8095 and 768 MiB before generating another plan and applying the changes.

## Project Files

Important project files include:

- BuildServer.java - Main Java application and logging implementation.
- BuildServerTest.java - JUnit tests.
- pom.xml - Maven build configuration.
- logging.properties - Logging configuration.
- Dockerfile - Docker container configuration.
- Jenkinsfile - Jenkins pipeline configuration.
- Kubernetes configuration files - Deployment resources.
- terraform/main.tf - Terraform Infrastructure as Code configuration.
- terraform/.terraform.lock.hcl - Terraform provider dependency lock file.
- .gitignore - Version control exclusions.

Generated build files, application logs, Terraform state files, and downloaded provider files are excluded from version control.

## Previous Coursework

The BuildProcessDemo application was originally developed and expanded through previous CEN4802 assignments.

The project incorporates work involving Java application development, Maven builds, JUnit testing, Jenkins continuous integration, Docker containerization, Kubernetes deployment, application logging, deployment automation, and Infrastructure as Code.

The Infrastructure as Code implementation reuses the existing BuildProcessDemo application and Docker image while introducing Terraform to manage the application's deployment infrastructure.