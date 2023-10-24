import sbt.file

lazy val projectName = "hexagonal-lib"

lazy val hexagonalLibJvm = publishableProject(projectName)
  .enablePlugins(LibProjectPlugin)
  .settings(
    name := "hexagonal-lib",
    organization := "com.tomshley.brands.global.tech.tware.products.hexagonal.lib",
    libraryDependencies ++= Seq(
      // Warning: Under Construction
    )
  )


lazy val hexagonalLib = (project in file("."))
  .enablePlugins(ProjectsHelperPlugin)
  .aggregate(
    hexagonalLibJvm
  )
