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
                    checkout scm
                    script {
                        Messages.checkOut(this)
                    }
                }
            }
            stage('Build in Docker') {
                steps {
                    script {
                        docker.image('openjdk:17').inside {
                            sh 'javac -version'  // Example build command
                            sh 'ls -la'          // Just to show file visibility
                        }
                    }
                }
            }
        }
    }
}
