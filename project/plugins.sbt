resolvers +=
  "gitlab-maven" at "https://gitlab.com/api/v4/projects/61834492/packages/maven"

addSbtPlugin(
  "com.tomshley.hexagonal" % "hexagonal-plugin-projecttemplate" % "0.0.10"
)
addSbtPlugin(
  "com.tomshley.hexagonal" % "hexagonal-plugin-projectsettings" % "0.0.10"
)
addSbtPlugin(
  "com.tomshley.hexagonal" % "hexagonal-plugin-projectstructure" % "0.0.10"
)
