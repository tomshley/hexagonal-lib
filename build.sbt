import sbt.file

lazy val projectName = "hexagonal-lib"

lazy val hexagonalLibJvm = publishableProject(projectName)
  .enablePlugins(HexagonalLibProjectPlugin)
  .settings(
    organization := "com.tomshley.brands.global.tech.tware.products.hexagonal.lib",
    libraryDependencies ++= Seq(
      // Warning: Under Construction
      "com.thesamet.scalapb" %% "compilerplugin" % "0.11.13",
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "0.11.13"
    )
  )


lazy val hexagonalLib = (project in file("."))
  .enablePlugins(ProjectsHelperPlugin)
  .aggregate(
    hexagonalLibJvm
  )
