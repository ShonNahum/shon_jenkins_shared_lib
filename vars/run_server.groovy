def call() {
    node {
        stage('Checkout') {
            checkout scm
        }
        stage('Build Docker Image') {
            def imageName = "shonnahum/sm:${env.BUILD_NUMBER}"
            sh "docker build -t ${imageName} ."
        }
        stage('Login to Artifactory') {
            withCredentials([usernamePassword(credentialsId: 'artifactory-creds-id', usernameVariable: 'ART_USER', passwordVariable: 'ART_PASS')]) {
                sh "docker login -u $ART_USER -p $ART_PASS "
            }
        }
        stage('Push Docker Image') {
            sh "docker push ${imageName}"
        }
    }
}
