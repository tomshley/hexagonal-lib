addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.0.0")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.13"

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")

addSbtPlugin(
  "com.tomshley.brands.global.tech.tware.products.hexagonal.plugins" %
    "hexagonal-plugin-projectsettings" %
    "0.1.0-SNAPSHOT"
)
