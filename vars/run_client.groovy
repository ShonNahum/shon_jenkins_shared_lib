def call() {
    pipeline {
        agent any
        stages {
            stage('Init') {
                steps {

                    echo "*******************************"
                    echo "Running from shared library! for client"
                    echo "*******************************"
                }
            }
        }
    }
}
