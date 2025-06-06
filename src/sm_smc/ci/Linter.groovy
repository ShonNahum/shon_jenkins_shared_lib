package sm_smc.ci

class Linter implements Serializable {
  def script
  Linter(script) { this.script = script }

  def run() {
    script.echo 'Running flake8...'
    script.sh 'flake8 .'
  }
}
