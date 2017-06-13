pipeline {
    agent any

    stages {
        stage('Build & Unit Test') {
            steps {
                sh "./gradlew -Dgradle.user.home=$HOME/.gradle :auth-server:clean :auth-server:build"
                step([$class: "JUnitResultArchiver", testResults: "auth-server/build/test-results/test/**/TEST-*.xml"])
            }
        }

        stage('Integration Test') {
            steps {
                sh "./gradlew -Dgradle.user.home=$HOME/.gradle :auth-server:integrationtest"
                step([$class: "JUnitResultArchiver", testResults: "auth-server/build/test-results/integrationTest/**/TEST-*.xml"])
            }
        }
    }
}