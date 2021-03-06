import sbt._
import Keys._

object ApplicationBuild extends Build {

  val appName         = "play2-neo4j"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.neo4j" % "neo4j" % "2.0.0",
    "com.sun.jersey" % "jersey-core" % "1.9",
    "ch.qos.logback" % "logback-core" % "1.0.3" force(), // this should override the Play version
    "ch.qos.logback" % "logback-classic" % "1.0.3" force(),
    "org.neo4j.app" % "neo4j-server" % "2.0.0" classifier "static-web" classifier "" exclude("org.slf4j", "slf4j-jdk14")
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("neo4j-release", url("http://m2.neo4j.org/content/repositories/releases"))(Resolver.mavenStylePatterns),
    resolvers += Resolver.url("neo4j-public-repository", url("http://m2.neo4j.org/content/groups/public"))(Resolver.mavenStylePatterns)
  )

}
