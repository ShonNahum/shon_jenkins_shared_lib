package sm_smc.ci

class DockerBuilder {
    static void buildAndTag(script) {
        // Get repo name from SCM URL
        def repoUrl = script.scm?.userRemoteConfigs?.getAt(0)?.url ?: 'unknown'
        def repoName = repoUrl.tokenize('/').last().replace('.git', '')
        script.echo "Repo name: ${repoName}"

        // Version tracking (fallback: 1)
        def versionFile = 'version.txt'
        def tag = 1

        if (script.fileExists(versionFile)) {
            tag = script.readFile(versionFile).trim().toInteger() + 1
        }

        // Save the new version
        script.writeFile(file: versionFile, text: tag.toString())

        // Build Docker image
        def imageTag = "${repoName}:${tag}"
        script.echo "Building Docker image: ${imageTag}"

        script.sh "docker build -t ${imageTag} ."

        // Optional: push to registry here
    }
}
