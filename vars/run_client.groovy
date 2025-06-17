import sm_smc.ci.Messages
import sm_smc.ci.DockerBuilder
import sm_smc.ci.PyLintRunner
import sm_smc.ci.PRHelper
import sm_smc.ci.GitHelper

def call() {
    pipeline {
        agent any
        environment {
            BASE_BRANCH = "main"
            GITHUB_REPO = "ShonNahum/SMC"
            GITHUB_TOKEN = credentials('github-token')
        }
        stages {
            stage('Checkout Code') {
                steps {
                    script {
                        Messages.checkOut(this)
                    }
                    checkout scm
                }
            }
            stage('Run Pylint') {
                steps {
                    script {
                        def lintPassed = PyLintRunner.run(this)
                        def repoName = GitHelper.getRepoName(this)

                        if (!lintPassed && env.BRANCH_NAME != 'main') {
                            PRHelper.createPullRequest(
                                this,
                                env.BRANCH_NAME,
                                env.BASE_BRANCH,
                                repoName,
                                env.GITHUB_TOKEN
                            )
                            error("❌ Pylint failed. Created PR to ${env.BASE_BRANCH}, stopping pipeline.")
                        } else {
                            echo "✅ Pylint passed. Continuing with pipeline."
                        }
                    }
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
