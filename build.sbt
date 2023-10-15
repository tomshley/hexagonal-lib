import sbt.file

lazy val projectName = "hexagonal-lib"
lazy val hexagonalLib = {
  HexagonalProjectSettings(projectName).project.enablePlugins(ProjectSettingsPlugin)
}

lazy val projects = (project in file("."))
  .enablePlugins(ProjectSettingsPlugin)
  .aggregate(
    hexagonalLib
  )
