import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "neo4j-scala"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.wordnik" %% "swagger-play2" % "1.3.2",
    "com.github.fge" % "json-schema-validator" % "2.1.7"
  )

  val play2neo4jModule = RootProject(file("../../module"))


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("fge-json-schema", url("http://dl.bintray.com/fge/maven"))(Resolver.mavenStylePatterns)
  ).dependsOn(play2neo4jModule).aggregate(play2neo4jModule)

}
