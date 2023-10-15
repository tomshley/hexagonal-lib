import com.tomshley.brands.global.tech.tware.products.hexagonal.plugins.common.model.Lib
import sbt.file

lazy val projectName = "hexagonal-lib"
lazy val hexagonalLib = {
  HexagonalProjectSettings(projectName).project.enablePlugins(ProjectSettingsPlugin).settings(
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "compilerplugin" % "0.11.13",
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "0.11.13"
    )
  )
}

lazy val projects = (project in file("."))
  .enablePlugins(ProjectSettingsPlugin)
  .aggregate(
    hexagonalLib
  )
