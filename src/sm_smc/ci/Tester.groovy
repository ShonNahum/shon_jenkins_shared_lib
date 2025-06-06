package sm_smc.ci

class Tester implements Serializable {
  def script
  Tester(script) { this.script = script }

  def run() {
    script.echo 'Running pytest...'
    script.sh 'pytest tests/'
  }
}
