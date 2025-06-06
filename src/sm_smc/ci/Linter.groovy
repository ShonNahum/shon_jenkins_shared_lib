package sm_smc.ci

class Linter implements Serializable {
  def script

  Linter(script) {
    this.script = script
  }

  def run() {
    // Write shared config file
    script.writeFile file: '.flake8', text: script.libraryResource('flake8.cfg')

    script.sh 'pip install flake8'
    script.sh 'flake8 .'
  }
}
