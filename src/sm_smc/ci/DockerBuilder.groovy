package sm_smc.ci

class DockerBuilder {

    static String buildImage(script, String repoUrl, String branchName, String dockerTag) {
        def repoName = repoUrl.tokenize('/').last().replace('.git', '').toLowerCase()
        def registry = 'shonnahum'

        def imageFullName = "${registry}/${repoName}:${dockerTag}"

        script.echo "🔨 Building Docker image: ${imageFullName}"
        script.sh "docker build -t ${imageFullName} ."

        return imageFullName
    }

    static void pushImage(script, String imageFullName) {
        script.withCredentials([
            script.usernamePassword(
                credentialsId: 'docker-artifactory-login',
                usernameVariable: 'USER',
                passwordVariable: 'PASS'
            )
        ]) {
            script.sh "echo \$PASS | docker login -u \$USER --password-stdin"
        }

        script.echo "📤 Pushing Docker image: ${imageFullName}"
        script.sh "docker push ${imageFullName}"

        script.echo "🧹 Removing local image: ${imageFullName}"
        script.sh "docker rmi ${imageFullName} || true"
    }
}
