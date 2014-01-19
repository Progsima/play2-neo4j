import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play2-neo4j-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "play2-neo4j" % "play2-neo4j_2.10" % "1.0-SNAPSHOT",
    "ch.qos.logback" % "logback-core" % "1.0.3" force(), // this should override the Play version
    "ch.qos.logback" % "logback-classic" % "1.0.3" force(),
    "org.neo4j.app" % "neo4j-server" % "2.0.0" classifier "static-web" classifier "" exclude("org.slf4j", "slf4j-jdk14")
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // after having running publish-local in the plugin using SBT, the jar will be put in the ivy2 repo  
    //   >> if so, uncomment the next line to set your local ivy2 repo as a new resolver
    // resolvers += Resolver.file("Local ivy2 Repository", file(Path.userHome.absolutePath+"/.ivy2/local"))(Resolver.ivyStylePatterns)
  )

}
