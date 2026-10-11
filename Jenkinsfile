pipeline {
    agent any

    tools {
        jdk 'JDK-21'
        maven 'Maven-3'
    }

    options {
        skipDefaultCheckout(true)
    }

    stages {

        // Retrieve the latest source code from GitHub.
        stage('Checkout Source') {
            steps {
                script {
                    def checkoutInfo = checkout scm
                    echo "Building Git revision: ${checkoutInfo.GIT_COMMIT}"
                }
            }
        }

        // Compile, test, and package the Java application.
        stage('Build, Test, and Package') {
            steps {
                bat 'mvn -B clean package'
            }
        }

        // Build a Docker image only after Maven succeeds.
        stage('Build Docker Image') {
            steps {

                // Verify Docker is accessible to Jenkins.
                bat '"C:\\Users\\Owner\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" version'

                // Build the image using the packaged JAR.
                bat '"C:\\Users\\Owner\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" build -t buildprocessdemo:midterm-%BUILD_NUMBER% .'
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: false
        }

        success {
            archiveArtifacts artifacts: 'target/*.jar',
                             fingerprint: true
        }
    }
}
