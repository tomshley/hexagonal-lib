resolvers +=
  "Artifactory" at "https://tomshleytech.jfrog.io/artifactory/hexagonal-sbt/"

addSbtPlugin(
  "com.tomshley.hexagonal" % "hexagonal-plugin-projecttemplate" % "0.0.8"
)
addSbtPlugin(
  "com.tomshley.hexagonal" % "hexagonal-plugin-projectsettings" % "0.0.8"
)
addSbtPlugin(
  "com.tomshley.hexagonal" % "hexagonal-plugin-projectstructure" % "0.0.8"
)
