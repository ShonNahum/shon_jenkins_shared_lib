def call() {
  def config = [
    appName: "smc",
    imageName: "shonnahum/smc:${env.BUILD_NUMBER}"
  ]

  def pipeline = new sm_smc.ci.logic(this, config)
  pipeline.run()
}
