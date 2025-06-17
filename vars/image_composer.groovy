sm_smc.ci.DockerBuilder
def call() {
    properties([
        parameters([
            string(name: 'REPO_URL', description: 'Git repo'),
            string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch'),
            string(name: 'DOCKER_TAG', defaultValue: 'latest', description: 'Docker image tag')
        ])
    ])

    pipeline {
        agent any
        stages {
            stage('Checkout') {
                steps {
                    git branch: params.BRANCH_NAME, url: params.REPO_URL, credentialsId: 'git'
                }
            }
            stage('Build & Push Docker') {
                steps {
                    script {
                        def image = DockerBuilder.buildImage(this, params.REPO_URL, params.BRANCH_NAME, params.DOCKER_TAG)
                        DockerBuilder.pushImage(this, image)
                    }
                }
            }
        }
    }
}
