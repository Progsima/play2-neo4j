import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "neo4j-scala"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Default Neo4j plugin deps
    "org.neo4j.app" % "neo4j-server" % "2.0.0" classifier "static-web" classifier "" exclude("org.slf4j", "slf4j-jdk14"),
    "com.wordnik" %% "swagger-play2" % "1.3.2"
  )

  val play2neo4jModule = RootProject(file("../../module"))


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // after having running publish-local in the plugin using SBT, the jar will be put in the ivy2 repo
    //   >> if so, uncomment the next line to set your local ivy2 repo as a new resolver
    // resolvers += Resolver.file("Local ivy2 Repository", file(Path.userHome.absolutePath+"/.ivy2/local"))(Resolver.ivyStylePatterns)
  ).dependsOn(play2neo4jModule).aggregate(play2neo4jModule)

}
