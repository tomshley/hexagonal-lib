import sbt.file

lazy val projectName = "hexagonal-lib"

lazy val hexagonalLib = publishableProject(projectName)
  .enablePlugins(HexagonalLibProjectPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "compilerplugin" % "0.11.13",
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "0.11.13"
    )
  )


lazy val projects = (project in file("."))
  .enablePlugins(ProjectsHelperPlugin)
  .aggregate(
    hexagonalLib
  )
