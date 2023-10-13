import sbt.Def
import sbt.Keys.crossScalaVersions

object Scala3 {

  val settings: Seq[Def.Setting[Seq[String]]] = Seq(crossScalaVersions := Dependencies.ScalaVersions)

}
