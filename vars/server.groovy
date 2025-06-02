def call() {
    pipeline {
        agent any
        stages {
            stage('Init') {
                steps {
                    echo "Running from shared library!"
                }
            }
        }
    }
}
