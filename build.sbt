import sbt.file

lazy val libProjectName = "hexagonal-lib"
lazy val libServersCommonProjectName = "hexagonal-lib-runmainasfuture-common"
lazy val libServersGRPCProjectName = "hexagonal-lib-runmainasfuture-grpc"
lazy val libServersHttpProjectName = "hexagonal-lib-runmainasfuture-http"
lazy val examplesProjectName = "hexagonal-lib-examples"
lazy val libProjectOrgName = "com.tomshley.brands.global.tech.tware.products.hexagonal.lib"
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
    organization := libProjectOrgName,
    libraryDependencies ++= Seq(
      // Warning: Under Construction
    )
  )
  .settings(protocSettings *)

lazy val libServersCommonProject = publishableProject(libServersCommonProjectName)
  .enablePlugins(LibProjectAkkaPlugin)
  .dependsOn(libProject)
  .settings(
    name := libServersCommonProjectName,
    organization := libProjectOrgName,
    libraryDependencies ++= Seq(
      // Warning: Under Construction
    )
  )
  .settings(protocSettings *)

lazy val libServersGRPCProject = publishableProject(libServersGRPCProjectName)
  .enablePlugins(LibProjectAkkaGrpcPlugin)
  .dependsOn(libServersCommonProject)
  .settings(
    name := libServersGRPCProjectName,
    organization := libProjectOrgName,
    libraryDependencies ++= Seq(
      // Warning: Under Construction
    )
  )
  .settings(protocSettings *)

lazy val libServersHttpProject = publishableProject(libServersHttpProjectName)
  .enablePlugins(LibProjectAkkaHttpPlugin)
  .dependsOn(libServersCommonProject)
  .settings(
    name := libServersHttpProjectName,
    organization := libProjectOrgName,
    libraryDependencies ++= Seq(
      // Warning: Under Construction
    )
  )
  .settings(protocSettings *)

lazy val examplesProject = internalProject(examplesProjectName)
  .enablePlugins(LibProjectPlugin, ProtocPlugin)
  .dependsOn(libProject)
  .settings(
    name := examplesProjectName,
    organization := "com.tomshley.brands.global.tech.tware.products.hexagonal.examples",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.9.0-M2",
      "io.grpc" % "grpc-netty" % "1.59.0",
      //       Note: the compiler plugin needs to be added to plugins.sbt "com.thesamet.scalapb" %% "compilerplugin" % scalaPBVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "0.11.13",
      "com.typesafe.akka" %% "akka-protobuf-v3" % "2.9.0-M2",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.9.0-M2",
      "com.typesafe.akka" %% "akka-stream" % "2.9.0-M2",
      "com.typesafe.akka" %% "akka-http" % "10.6.0-M1",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.6.0-M1" % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" %  "2.9.0-M2" % Test,
      "com.typesafe.akka" %% "akka-testkit" % "2.9.0-M2" % Test,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    ),
  )
  .settings(protocSettings *)
  .settings(
    publish / skip := true
  )
  .dependsOn(libProject)

lazy val hexagonalLib = (project in file("."))
  .enablePlugins(ProjectsHelperPlugin)
  .aggregate(
    libProject,
    libServersCommonProject,
    libServersGRPCProject,
    libServersHttpProject,
    examplesProject
  )
  .settings(
    publish / skip := true
  )
  .settings(protocSettings *)
