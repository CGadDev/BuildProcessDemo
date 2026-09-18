pipeline {
    agent any

    tools {
        maven 'Maven-3'
    }

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build, Test, and Package') {
            steps {
                bat 'mvn -B clean package'
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