resolvers +=
  "gitlab-maven" at "https://gitlab.com/api/v4/projects/61841284/packages/maven"

addSbtPlugin(
  "com.tomshley.hexagonal" % "hexagonal-plugin-projectsettings" % "0.0.16"
)