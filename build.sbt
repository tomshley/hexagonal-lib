import sbt.file
lazy val gitlabCIProjectId = 61841284

lazy val libProjectName = "hexagonal-lib"
lazy val hexagonalProjectOrgName = "com.tomshley.hexagonal"
lazy val publishSettings = Seq(
  ThisBuild / resolvers ++= Registry.additionalResolvers(gitlabCIProjectId),
  ThisBuild / credentials += Registry.credentials(
    Some((ThisBuild / baseDirectory).value / ".credentials.gitlab")
  ),
  ThisBuild / publishTo := Registry.publishToGitlab(gitlabCIProjectId)
)
lazy val libProject = publishableProject(libProjectName)
  .enablePlugins(ValueAddProjectPlugin)
  .settings(
    organization := hexagonalProjectOrgName,
    version := "0.0.14",
    publishConfiguration := publishConfiguration.value.withOverwrite(true)
  )
  .settings(publishSettings *)

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(
    ProjectTemplatePlugin,
    ProjectsHelperPlugin,
    ProjectStructurePlugin
  )
  .aggregate(libProject)
  .settings(publish / skip := true)
  .settings(publishSettings *)
