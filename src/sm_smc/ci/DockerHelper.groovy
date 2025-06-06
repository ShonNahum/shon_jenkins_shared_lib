package sm_smc.ci

class DockerHelper implements Serializable {
  def script
  def imageName

  DockerHelper(script, imageName) {
    this.script = script
    this.imageName = imageName
  }

  def build() {
    script.echo "Building Docker image: ${imageName}"
    script.sh "docker build -t ${imageName} ."
  }

  def push() {
    script.withCredentials([script.usernamePassword(
      credentialsId: 'artifactory-creds-id',
      usernameVariable: 'USER',
      passwordVariable: 'PASS'
    )]) {
      script.sh """
        echo "\$PASS" | docker login -u "\$USER" --password-stdin // i use dockerhub artifactory
        docker push ${imageName}
      """
    }
  }
}
