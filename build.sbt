//import com.tomshley.brands.global.tech.tware.products.hexagonal.plugins.common.model.ValueAdd
//import com.tomshley.brands.global.tech.tware.products.hexagonal.plugins.projectstructure.ProjectStructurePlugin.autoImport.hexagonalPart

import sbt.file
lazy val libProject = hexagonalLibProject("hexagonal-lib", Dependencies.javaProject, Dependencies.libProject, Scala3.settings)

lazy val projects = (project in file("."))
  .aggregate(
    libProject
  )
  .settings(
    scalaVersion := Dependencies.Scala3
  )

def hexagonalLibProject(projectName: String, additionalSettings: sbt.Def.SettingsDefinition*): Project = {
  Project(id = projectName, base = file(projectName))
    .settings(
      organization := "com.tomshley.brands.global.tech.tware.products.hexagonal",
      name := projectName,
      licenses := {
        val tagOrBranch =
          if (version.value.endsWith("SNAPSHOT")) "main"
          else "v" + version.value
        Seq(("APACHE-2.0", url("https://raw.githubusercontent.com/tomshley/hexagonal-plugins-sbt/" + tagOrBranch + "/LICENSE")))
      },
      scalacOptions += "-Wconf:cat=deprecation&msg=.*JavaConverters.*:s",
      scalaVersion := Dependencies.Scala3
    )
    .settings(additionalSettings *)
}
