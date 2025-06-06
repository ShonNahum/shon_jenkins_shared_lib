package sm_smc.ci

class StructurePipeline implements Serializable {
  def script
  def config

  StructurePipeline(script, config) {
    this.script = script
    this.config = config
  }

  def run() {
    script.pipeline {
      script.agent any

      script.environment {
        IMAGE_NAME = config.imageName
      }

      script.stages {
        script.stage('Checkout') {
          script.steps {
            script.checkout script.scm
          }
        }

        script.stage('Install Requirements') {
          script.steps {
            script.sh 'pip install -r requirements.txt'
          }
        }

        script.stage('Lint') {
          script.steps {
            def linter = new Linter(script)
            linter.run()
          }
        }

        script.stage('Unit Test') {
          script.steps {
            def tester = new Tester(script)
            tester.run()
          }
        }

        script.stage('Docker Build & Push') {
          script.steps {
            def docker = new DockerHelper(script, config)
            docker.buildAndPush()
          }
        }
      }

      script.post {
        script.always {
          script.sh 'docker image prune -f || true'
        }
      }
    }
  }
}
