
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

        // Create a Docker image only after the Maven build succeeds.
        stage('Build Docker Image') {
            steps {
                bat 'docker build -t buildprocessdemo:midterm-%BUILD_NUMBER% .'
            }
        }
    }

    post {
        // Publish JUnit results even when tests fail.
        always {
            junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: false
        }

        // Archive the packaged JAR after a successful pipeline.
        success {
            archiveArtifacts artifacts: 'target/*.jar',
                             fingerprint: true
        }
    }
}
