pipeline {
    agent any

    stages {
        stage('Build & Unit Test') {
            dir('auth-server') {
                steps {
                    sh "./gradlew -Dgradle.user.home=$HOME/.gradle clean build"
                    step([$class: "JUnitResultArchiver", testResults: "build/test-results/test/**/TEST-*.xml"])
                }
            }
        }

        stage('Integration Test') {
            dir('auth-server') {
                steps {
                    sh "./gradlew -Dgradle.user.home=$HOME/.gradle integrationtest"
                    step([$class: "JUnitResultArchiver", testResults: "build/test-results/integrationTest/**/TEST-*.xml"])
                }
            }
        }
    }
}
