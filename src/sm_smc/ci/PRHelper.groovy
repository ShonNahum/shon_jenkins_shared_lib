package sm_smc.ci

class PRHelper {
    static void createPullRequest(script, String source, String target, String repo, String token) {
        def body = """
        {
          "title": "Fix: ${source} → ${target}",
          "head": "${source}",
          "base": "${target}",
          "body": "Auto-created PR due to lint failure"
        }
        """.stripIndent().trim()

        def cmd = """
        curl -s -X POST -H "Authorization: token ${token}" \\
             -H "Content-Type: application/json" \\
             -d '${body}' \\
             https://api.github.com/repos/${repo}/pulls
        """

        script.sh cmd
    }
}
