def call() {
  pipeline {
    agent {
      docker {
        image 'python:3.10'
        args '-u root:root'
      }
    }

    environment {
      IMAGE_NAME = "shonnahum/sm:${env.BUILD_NUMBER}"
    }

    stages {
      stage('Checkout') {
        steps {
          checkout scm
        }
      }

      stage('Install Requirements') {
        steps {
          sh 'pip install -r requirements.txt'
        }
      }

      stage('Lint') {
        steps {
          script {
            new sm_smc.ci.Linter(this).run()
          }
        }
      }

      stage('Unit Test') {
        steps {
          script {
            new sm_smc.ci.Tester(this).run()
          }
        }
      }

      stage('Build Docker') {
        steps {
          script {
            new sm_smc.ci.DockerHelper(this, env.IMAGE_NAME).build()
          }
        }
      }

      stage('Push Docker') {
        steps {
          script {
            new sm_smc.ci.DockerHelper(this, env.IMAGE_NAME).push()
          }
        }
      }
    }

    post {
      always {
        script {
          node {
            echo "Cleaning up..."
            try {
              sh 'docker image prune -f || true'
            } catch (err) {
              echo "Docker prune failed: ${err}"
            }
          }
        }
      }
    }
  }
}
