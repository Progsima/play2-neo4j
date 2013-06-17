import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play2-neo4j-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "play2-neo4j" % "play2-neo4j_2.10" % "1.0-SNAPSHOT",
    "ch.qos.logback" % "logback-core" % "1.0.3" force(), // this should override the Play version
    "ch.qos.logback" % "logback-classic" % "1.0.3" force()
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
