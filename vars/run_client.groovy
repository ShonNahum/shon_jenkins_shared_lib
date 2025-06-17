import sm_smc.ci.Test

def call() {
    pipeline {
        agent any
        stages {
            stage('Run Client') {
                steps {
                    script {
                        Test.sayHello()
                    }
                }
            }
        }
    }
}
