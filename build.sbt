import sbt.file

lazy val libProjectName = "hexagonal-lib"
lazy val examplesProjectName = "hexagonal-lib-examples"
lazy val protocSettings = Seq(
  Compile / PB.protoSources := Seq(
    sourceDirectory.value / "main" / "proto"
  ),
  Compile / PB.targets := Seq(
    scalapb.gen() -> (Compile / sourceManaged).value
  ),
  PB.protocExecutable := file("/opt/homebrew/Cellar/protobuf/24.4/bin/protoc-24.4.0")
)

lazy val libProject = publishableProject(libProjectName)
  .enablePlugins(LibProjectPlugin)
  .settings(
    name := libProjectName,
    organization := "com.tomshley.brands.global.tech.tware.products.hexagonal.lib",
    libraryDependencies ++= Seq(
      // Warning: Under Construction
    )
  )
  .settings(protocSettings *)

lazy val examplesProject = internalProject(examplesProjectName)
  .enablePlugins(CoreProjectPlugin, ProtocPlugin)
  .dependsOn(libProject)
  .settings(
    name := examplesProjectName,
    organization := "com.tomshley.brands.global.tech.tware.products.hexagonal.examples",
    libraryDependencies ++= Seq(
      // Warning: Under Construction
    ),
  )
  .settings(protocSettings *)
  .dependsOn(libProject)

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(ProjectsHelperPlugin)
  .aggregate(
    libProject,
    examplesProject
  )
  .settings(protocSettings *)
