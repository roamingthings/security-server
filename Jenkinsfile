node("linux && jdk8") {
    stage "Checkout"
    git url: "https://github.com/roamingthings/security-server.git"

    dir('auth-server') {
        stage "Build/Analyse/Test"
        sh "../gradlew clean build"
        archiveUnitTestResults()

        stage "Integration Test"
        sh "../gradlew integrationtest"
    }
}

def archiveUnitTestResults() {
    step([$class: "JUnitResultArchiver", testResults: "build/**/TEST-*.xml"])
}
