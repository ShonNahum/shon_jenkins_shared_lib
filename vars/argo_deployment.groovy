def call() {
    properties([
        parameters([
            string(name: 'SM_TAG', defaultValue: 'latest', description: 'Tag version for SM image'),
            string(name: 'SMC_TAG', defaultValue: 'latest', description: 'Tag version for SMC image')
        ])
    ])

    pipeline {
        agent any

        environment {
            CHART_REPO_URL = 'git@github.com:ShonNahum/sm_smc_Chart.git'
            CHART_REPO_CREDENTIALS = 'git'
            BRANCH = 'main'
        }

        stages {
            stage('Checkout Helm Chart Repo') {
                steps {
                    git branch: "${BRANCH}",
                        url: "${CHART_REPO_URL}",
                        credentialsId: "${CHART_REPO_CREDENTIALS}"
                }
            }

            stage('Update Image Tags') {
                steps {
                    script {
                        def valuesFile = 'values.yaml'

                        // Read the file
                        def text = readFile valuesFile

                        // Replace image tags
                        text = text.replaceAll(/sm:\s*image:\s*.*$/, "sm:\n  image: \"shonnahum/sm:${params.SM_TAG}\"")
                        text = text.replaceAll(/smc:\s*image:\s*.*$/, "smc:\n  image: \"shonnahum/smc:${params.SMC_TAG}\"")
                        echo "Content of file:\n${text}"
                        // Write back updated file
                        writeFile file: valuesFile, text: text

                        sh "git config user.email 'jenkins@example.com'"
                        sh "git config user.name 'Jenkins CI'"

                        sh "git add ${valuesFile}"
                        sh "git commit -m 'Update SM and SMC image tags to ${params.SM_TAG} and ${params.SMC_TAG}'"
                        sh "git push origin ${BRANCH}"
                    }
                }
            }
        }
    }
}
