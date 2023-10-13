import sbt.{Def, *}
import Keys.*

object Dependencies {
  val Scala213 = "2.13.12"
  val Scala212 = "2.12.18"
  val Scala3 = "3.3.1"
  val Scala2Versions: Seq[String] = Seq(Scala213, Scala212)
  val ScalaVersions: Seq[String] = Dependencies.Scala2Versions :+ Dependencies.Scala3

  lazy val springBootVersion = "3.1.4"
  lazy val jetbrainsAnnotationsVersion = "24.0.1"
  lazy val apacheCommonsLang3Version = "3.12.0"
  lazy val jodaTimeVersion = "2.12.5"
  lazy val googleGuavaVersion = "23.0"
  lazy val jacksonVersion = "2.15.2"
  lazy val slf4jVersion = "2.0.5"
  lazy val apacheCommonsIOVersion = "20030203.000550"
  lazy val apacheCommonsDigester = "3.2"

  val javaProject: Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-digester3" % apacheCommonsDigester,
      "commons-io" % "commons-io" % apacheCommonsIOVersion,
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "org.slf4j" % "slf4j-simple" % slf4jVersion,
      "org.jetbrains" % "annotations" % jetbrainsAnnotationsVersion,
      "org.apache.commons" % "commons-lang3" % apacheCommonsLang3Version,
      "joda-time" % "joda-time" % jodaTimeVersion,
      "com.google.guava" % "guava" % googleGuavaVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
    )
  )
  val libProject: Seq[Def.Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.32.0",
      "com.typesafe" % "config" % "1.4.2"
    )
  )
}
