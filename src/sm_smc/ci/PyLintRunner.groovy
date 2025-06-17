package sm_smc.ci

class PyLintRunner {
    static boolean run(script) {
        script.echo "Running pylint inside Python Docker container..."

        def result = script.docker.image('python:3.11-slim').inside {
            script.sh '''
                pip install --no-cache-dir -r requirements.txt
                pip install pylint > /dev/null
                echo "Running pylint..."
            ''' // install first

            // capture lint result
            return script.sh(script: 'pylint **/*.py --fail-under=7', returnStatus: true)
        }

        if (result != 0) {
            script.echo "❌ Pylint failed with score under threshold"
            return false
        }

        script.echo "✅ Pylint passed"
        return true
    }
}
