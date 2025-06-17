import sm_smc.ci.Messages
import sm_smc.ci.DockerBuilder

def call() {
    pipeline {
        agent any
        stages {
            stage('Checkout Code') {
                steps {
                    script {
                        Messages.checkOut(this)
                    }
                    checkout scm
                }
            }
            stage('Docker Build') {
                steps {
                    script {
                        env.IMAGE_NAME = DockerBuilder.buildImage(this)
                    }
                }
            }

            stage('Docker Push') {
                steps {
                    script {
                        DockerBuilder.pushImage(this, env.IMAGE_NAME)
                    }
                }
            }
        }
    }
}
