def call() {
  pipeline {
    agent any

    environment {
      IMAGE_NAME = "shonnahum/smc:${env.BUILD_NUMBER}"
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
            // call your Linter class
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
        echo "Cleaning up..."
        sh 'docker image prune -f || true'
      }
    }
  }
}
