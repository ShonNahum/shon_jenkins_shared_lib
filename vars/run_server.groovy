def call() {
    node {
        def imageName = "shonnahum/sm:${env.BUILD_NUMBER}"

        stage('Checkout') {
            checkout scm
        }

        stage('Build Docker Image') {
            sh 'docker --version'
            sh "docker build -t ${imageName} ."
        }

        stage('Login to Artifactory') {
            withCredentials([usernamePassword(credentialsId: 'artifactory-creds-id', usernameVariable: 'ART_USER', passwordVariable: 'ART_PASS')]) {
                sh "docker login -u $ART_USER -p $ART_PASS"
            }
        }

        stage('Push Docker Image') {
            sh "docker push ${imageName}"
        }
    }
}
