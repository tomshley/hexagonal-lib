import sbt.file

lazy val libProjectName = "hexagonal-lib"
lazy val hexagonalProjectOrgName = "com.tomshley.hexagonal"

lazy val libProject = publishableProject(libProjectName)
  .enablePlugins(ValueAddProjectPlugin, VersionFilePlugin, PublishGitLabPlugin)
  .settings(
    organization := hexagonalProjectOrgName,
    publishGitLabProjectId := 61841284
  )

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(
    ProjectsHelperPlugin
  )
  .aggregate(libProject)
  .settings(publish / skip := true)
