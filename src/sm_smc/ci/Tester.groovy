package sm_smc.ci

class Tester implements Serializable {
  def script

  Tester(script) {
    this.script = script
  }

  def run() {
    script.sh "echo This is a FAKE QA Test FOR NOW"
  }
}
