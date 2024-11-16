import sbt.file

import java.time.Instant
lazy val gitlabCIProjectId = 61841284


val versionFileValue = settingKey[String]("value from VERSION")


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
  .enablePlugins(ValueAddProjectPlugin, VersionFilePlugin)
  .settings(
    organization := hexagonalProjectOrgName,
    version := {
      val versionFile = (ThisBuild / baseDirectory).value / "VERSION"
      val versionFileContents: Seq[String] = if (versionFile.exists()) IO.readLines(versionFile) else if (version.value.nonEmpty) Seq(version.value) else Seq.empty[String]
      versionFileContents.filter(s => !s.isBlank).mkString("-").trim.stripPrefix("v")
    }
  )
  .settings(publishSettings *)

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(
    ProjectsHelperPlugin
  )
  .aggregate(libProject)
  .settings(publish / skip := true)
  .settings(publishSettings *)
