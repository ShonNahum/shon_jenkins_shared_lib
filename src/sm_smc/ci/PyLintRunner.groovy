package sm_smc.ci

class PyLintRunner implements Serializable {

    static void run(script) {
        script.echo "Running pylint inside Python Docker container..."

        script.docker.image('python:3.11-slim').inside {
            script.sh '''
                pip install --no-cache-dir -r requirements.txt
                pip install pylint > /dev/null
                echo "Running pylint..."
                pylint **/*.py --fail-under=10
            '''
        }
    }
}
