import sbt.file

lazy val libProjectName = "hexagonal-lib"
lazy val hexagonalProjectOrgName = "com.tomshley.hexagonal"

lazy val libProject = publishableProject(libProjectName)
  .enablePlugins(ValueAddProjectPlugin, VersionFilePlugin, PublishGitLabPlugin)
  .settings(
    organization := hexagonalProjectOrgName,
    publishGitLabProjectId := 61841284,
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
