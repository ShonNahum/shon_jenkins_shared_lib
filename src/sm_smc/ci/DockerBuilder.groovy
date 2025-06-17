package sm_smc.ci

class DockerBuilder {
    static String buildImage(script) {
        def repoUrl = script.scm?.userRemoteConfigs?.getAt(0)?.url ?: 'unknown'
        def repoName = repoUrl.tokenize('/').last().replace('.git', '').toLowerCase()

        def versionFile = 'version.txt'
        def tag = 1
        if (script.fileExists(versionFile)) {
            tag = script.readFile(versionFile).trim().toInteger() + 1
        }
        script.writeFile(file: versionFile, text: tag.toString())

        def registry = 'shonnahum'
        def imageFullName = "${registry}/${repoName}:${tag}"

        script.echo "Building Docker image: ${imageFullName}"
        script.sh "docker build -t ${imageFullName} ."

        return imageFullName  // Pass to push step
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

        script.echo "Pushing Docker image: ${imageFullName}"
        script.sh "docker push ${imageFullName}"

        script.echo "Removing local image: ${imageFullName}"
        script.sh "docker rmi ${imageFullName} || true"
    }


}
