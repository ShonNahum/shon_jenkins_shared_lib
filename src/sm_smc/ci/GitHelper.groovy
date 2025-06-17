package sm_smc.ci

class GitHelper {
    static String getRepoName(script) {
        def repoUrl = script.scm.getUserRemoteConfigs()[0].getUrl()
        return repoUrl.replaceAll(/^.*github.com[:\/]/, '').replaceAll(/\.git$/, '')
    }
}
