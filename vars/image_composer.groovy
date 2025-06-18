def call() {
    def repos = [
        SM : 'git@github.com:ShonNahum/SM.git',
        SMC: 'git@github.com:ShonNahum/SMC.git'
    ]

    properties([
        parameters([
            choice(name: 'REPO_URL', choices: [
                'git@github.com:ShonNahum/SM.git',
                'git@github.com:ShonNahum/SMC.git'
            ], description: 'Select the repository'),
            string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch name to build'),
            string(name: 'DOCKER_TAG', defaultValue: 'latest', description: 'Docker image tag'),
            booleanParam(name: 'RUN_BOTH_REPOS', defaultValue: false, description: 'Run for both SM and SMC in parallel')
        ])
    ])

    pipeline {
        agent any

        stages {
            stage('Checkout') {
                steps {
                    script {
                        if (params.RUN_BOTH_REPOS) {
                            parallel(
                                SM: {
                                    checkoutRepo(repos.SM, 'SM')
                                },
                                SMC: {
                                    checkoutRepo(repos.SMC, 'SMC')
                                }
                            )
                        } else {
                            checkoutRepo(params.REPO_URL, getRepoName(params.REPO_URL))
                        }
                    }
                }
            }

            stage('Build Docker') {
                steps {
                    script {
                        if (params.RUN_BOTH_REPOS) {
                            parallel(
                                SM: {
                                    buildImage(repos.SM, 'SM')
                                },
                                SMC: {
                                    buildImage(repos.SMC, 'SMC')
                                }
                            )
                        } else {
                            buildImage(params.REPO_URL, getRepoName(params.REPO_URL))
                        }
                    }
                }
            }

            stage('Push Docker') {
                steps {
                    script {
                        if (params.RUN_BOTH_REPOS) {
                            parallel(
                                SM: {
                                    pushImage('SM')
                                },
                                SMC: {
                                    pushImage('SMC')
                                }
                            )
                        } else {
                            pushImage(getRepoName(params.REPO_URL))
                        }
                    }
                }
            }
        }
    }
}

def checkoutRepo(repoUrl, repoName) {
    dir(repoName) {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${params.BRANCH_NAME}"]],
            userRemoteConfigs: [[url: repoUrl, credentialsId: 'git']]
        ])
    }
}

def buildImage(repoUrl, repoName) {
    dir(repoName) {
        def image = sm_smc.ci.DockerBuilder.buildImage(this, repoUrl, params.BRANCH_NAME, params.DOCKER_TAG)
        currentBuild.description = "Built ${repoName}:${params.DOCKER_TAG}"
        return image
    }
}

def pushImage(repoName) {
    dir(repoName) {
        def image = "shonnahum/${repoName.toLowerCase()}:${params.DOCKER_TAG}"
        sm_smc.ci.DockerBuilder.pushImage(this, image)
    }
}

def getRepoName(url) {
    return url.tokenize('/').last().replace('.git', '')
}
