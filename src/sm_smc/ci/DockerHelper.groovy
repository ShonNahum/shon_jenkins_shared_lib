package sm_smc.ci

class DockerHelper implements Serializable {
  def script
  def config

  DockerHelper(script, config) {
    this.script = script
    this.config = config
  }

  def buildAndPush() {
    // Build Docker image
    script.sh "docker build -t ${config.imageName} ."

    // Login to Artifactory using credentials stored in Jenkins
    script.withCredentials([
      script.usernamePassword(
        credentialsId: 'artifactory-creds-id', 
        usernameVariable: 'ARTIFACTORY_USER',
        passwordVariable: 'ARTIFACTORY_PASS'
      )
    ]) {
      script.sh """
        echo "$ARTIFACTORY_PASS" | docker login your-artifactory.com -u "$ARTIFACTORY_USER" --password-stdin
        docker push ${config.imageName}
      """
    }
  }
}
