pipeline {
  agent any

  environment {
    IMAGE_NAME = "shonnahum/sm:${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Install Requirements & Lint & Test') {
      steps {
        // Run all python related commands inside a python container with current workspace mounted
        script {
          docker.image('python:3.10').inside('-u root:root') {
            sh 'pip install --upgrade pip'
            sh 'pip install -r requirements.txt'
            sh 'flake8 .'     // Lint example
            sh 'pytest tests' // Unit test example
          }
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
