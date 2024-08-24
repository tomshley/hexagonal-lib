import sbt.file

lazy val libProjectName = "hexagonal-lib"
lazy val hexagonalProjectOrgName = "com.tomshley.hexagonal"

lazy val libProject = publishableProject(libProjectName)
  .enablePlugins(
    ValueAddProjectPlugin
  )
  .settings(
    resolvers +=
      "Artifactory" at "https://tomshleytech.jfrog.io/artifactory/hexagonal-sbt/",
    organization := hexagonalProjectOrgName,
    version := "0.0.8",
    publishTo := {
      val buildInfo =
        if (version.value.endsWith("SNAPSHOT"))
          ";build.timestamp=" + new java.util.Date().getTime
        else ""
      Some(
        "Artifactory Realm" at s"https://tomshleytech.jfrog.io/artifactory/tomshley-brands-global-tware-tech-products-hexagonal-sbt${buildInfo}"
      )
    }
  )

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(
    ProjectTemplatePlugin,
    ProjectsHelperPlugin,
    ProjectStructurePlugin
  )
  .aggregate(libProject)
  .settings(publish / skip := true)
