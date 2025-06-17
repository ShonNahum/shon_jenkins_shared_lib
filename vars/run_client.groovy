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
            stage('Check Branch') {
                steps {
                    script {
                        if (env.BRANCH_NAME == 'main') {
                            echo "Pipeline skipped on main branch."
                            currentBuild.result = 'NOT_BUILT' 
                            error("Aborting pipeline on main branch. - Building on main Branch ARE not allowed")
                        } else {
                            echo "Running pipeline on branch: ${env.BRANCH_NAME}"
                        }
                    }
                }
            }
            stage('Checkout Code') {
                steps {
                    
                    script {
                        Messages.checkOut(this)
                    }
                    checkout scm
                }
            }
            stage('Run Pylint & Pull Request If needed') {
                steps {
                    script {
                        def lintPassed = PyLintRunner.run(this)
                        def repoName = GitHelper.getRepoName(this)

                        if (lintPassed) {
                            PRHelper.createPullRequest(
                                this,
                                env.BRANCH_NAME,
                                env.BASE_BRANCH,
                                repoName,
                                env.GITHUB_TOKEN
                            )
                            echo "❌ Pylint Passed. Created PR to ${env.BASE_BRANCH}."
                        } else {
                            echo "✅ Pylint Failed."
                            error("❌ Pylint failed. Stopping pipeline. - Get Better Pylint Rate please")
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
