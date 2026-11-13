terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "4.6.0"
    }
  }
}

# Connect Terraform to Docker Desktop on Windows.
provider "docker" {
  host = "npipe:////.//pipe//docker_engine"
}

# Reference the existing BuildProcessDemo image.
data "docker_image" "build_demo" {
  name = "build-process-demo:v2"
}

# Create a dedicated Docker network.
resource "docker_network" "demo_network" {
  name   = "build-demo-iac-network"
  driver = "bridge"
}

# Create and manage the BuildProcessDemo container.
# Modified memory allocation and external port for Section 4.
resource "docker_container" "demo" {
  name   = "build-demo-iac"
  image  = data.docker_image.build_demo.id
  memory = 768

  ports {
    internal = 8081
    external = 8095
    ip       = "127.0.0.1"
  }
  networks_advanced {
    name = docker_network.demo_network.name
  }
}

# Display the updated application URL.
output "application_url" {
  value = "http://localhost:8095"
}
