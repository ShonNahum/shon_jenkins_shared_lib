def call() {
    properties([
        parameters([
            choice(name: 'REPO_URL', choices: [
                'git@github.com:ShonNahum/SM.git',
                'git@github.com:ShonNahum/SMC.git'
            ], description: 'Select the repository'),
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
                        def image = sm_smc.ci.DockerBuilder.buildImage(this, params.REPO_URL, params.BRANCH_NAME, params.DOCKER_TAG)
                        sm_smc.ci.DockerBuilder.pushImage(this, image)
                    }
                }
            }
        }
    }
}
