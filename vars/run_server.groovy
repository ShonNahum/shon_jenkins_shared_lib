def call() {
  def config = [
    appName: "sm",
    imageName: "shonnahum/sm:${env.BUILD_NUMBER}"
  ]

  def pipeline = new sm_smc.ci.StructurePipeline(this, config)
  pipeline.run()
}
