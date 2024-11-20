import sbt.file

lazy val libProjectName = "hexagonal-lib"
lazy val hexagonalProjectOrgName = "com.tomshley.hexagonal"

lazy val libProject = publishableProject(libProjectName)
  .enablePlugins(ValueAddProjectPlugin, VersionFilePlugin, PublishGitLabPlugin)
  .settings(
    organization := hexagonalProjectOrgName,
    publishGitLabProjectId := 61841284,
    libraryDependencies += "com.twilio.sdk" % "twilio" % "10.4.1"
  )

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(
    ProjectsHelperPlugin
  )
  .aggregate(libProject)
  .settings(publish / skip := true)
