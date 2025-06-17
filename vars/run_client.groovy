import sm_smc.ci.Messages

def call() {
    pipeline {
        agent any
        stages {
            stage('Run Client') {
                steps {
                    script {
                        Messages.sayHello(this)
                    }
                }
            }
            stage('Checkout Code') {
                steps {
                    script {
                        Messages.checkOut(this)
                    }
                    checkout scm

                }
            }

        }
    }
}
