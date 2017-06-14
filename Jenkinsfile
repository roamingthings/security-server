pipeline {
    agent any

    stages {
        stage('Build & Unit Test') {
            steps {
                sh "./gradlew -Dgradle.user.home=$HOME/.gradle :auth-server:clean :auth-server:build"
            }
        }

        stage('Integration Test') {
            steps {
                sh "./gradlew -Dgradle.user.home=$HOME/.gradle :auth-server:integrationtest"
            }
        }
    }
    post {
        always {
            step([$class: "JUnitResultArchiver", testResults: "auth-server/build/test-results/**/TEST-*.xml"])
        }
    }
}