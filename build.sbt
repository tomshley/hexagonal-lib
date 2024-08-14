import sbt.file

lazy val libProjectName = "hexagonal-lib"
lazy val hexagonalProjectOrgName = "com.tomshley.hexagonal"

lazy val libProject = publishableProject(libProjectName)
  .enablePlugins(
    ProjectTemplatePlugin,
    HexagonalProjectPlugin,
    ProjectStructurePlugin,
    ValueAddProjectPlugin
  )
  .settings(name := libProjectName, organization := hexagonalProjectOrgName)

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(
    ProjectTemplatePlugin,
    HexagonalProjectPlugin,
    ProjectStructurePlugin
  )
  .aggregate(libProject)
  .settings(publish / skip := true)
