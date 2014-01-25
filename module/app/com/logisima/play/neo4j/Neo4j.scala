package com.logisima.play.neo4j

import play.api.Play
import play.Logger
import play.api.Play.current

import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.kernel.GraphDatabaseAPI
import org.neo4j.server.configuration.{Configurator, ServerConfigurator}
import org.neo4j.server.WrappingNeoServerBootstrapper

import org.neo4j.shell.ShellSettings
import org.neo4j.helpers.Settings
import com.logisima.play.neo4j.utils.FileUtils
import java.io.File

/**
 * Neo4j database object.
 *
 * @author : bsimard
 */
object Neo4j {

  /**
   * Graph database
   */
  var graphdb: Option[GraphDatabaseAPI] = None

  /**
   * Server for webadmin
   */
  private var webadmin: Option[WrappingNeoServerBootstrapper] = None

  /**
   * Neo4j properties files path
   */
  private val neo4jPropertiesPath: String = "conf/neo4j.properties"

  /**
   * Neo4j server url
   */
  private var neo4jUrl: String = "http://localhost:" + Play.configuration.getInt("neo4j.embedded.port").getOrElse(7575)

  /**
   * Starting neo4j database.
   *
   * @return
   */
  private def startDb() = {
    val DBPath: String = Play.configuration.getString("neo4j.embedded.path").getOrElse("neo4j")
    Logger.debug("Neo4j database path is : " + DBPath)

    // Create the neo4j database
    val graphdb = FileUtils.getFile(neo4jPropertiesPath) match {
      case Some(f: File) => {
        new GraphDatabaseFactory()
          .newEmbeddedDatabaseBuilder(DBPath)
          .loadPropertiesFromFile(neo4jPropertiesPath)
          .newGraphDatabase()
          .asInstanceOf[GraphDatabaseAPI]
      }
      case None => {
        new GraphDatabaseFactory()
          .newEmbeddedDatabaseBuilder(DBPath)
          .setConfig(ShellSettings.remote_shell_enabled, Settings.TRUE)
          .newGraphDatabase()
          .asInstanceOf[GraphDatabaseAPI]
      }
    }

    // create the neo4j server
    val config = new ServerConfigurator(graphdb);
    // let the server endpoint be on a custom port
    config.configuration().setProperty(
      Configurator.WEBSERVER_PORT_PROPERTY_KEY,
      Play.configuration.getInt("neo4j.embedded.port").getOrElse(7575)
    )

    val srv = new WrappingNeoServerBootstrapper(graphdb, config);

    (graphdb, srv)
  }

  /**
   * Starting webadmin server.
   *
   * @return
   */
  private def startAdmin() = {
    Logger.debug("Starting webadmin")
    webadmin.map {
      server =>
        server.start()
    }
  }

  /**
   * Start the database with primary nodes.
   */
  def start() {
    Logger.debug("init database")

    Play.configuration.getString("neo4j.url") match {

      // Configure for a distant neo4j server
      case Some(url: String) => {
        neo4jUrl = url
      }

      // Configure and start an embedded server
      case None => {
        Some(startDb()).map {
          case (x, y) =>
            graphdb = Some(x)
            webadmin = Some(y)
        }
        startAdmin()
      }
    }
  }

  /**
   * Stopping server & admin server
   */
  def stop() {
    Logger.debug("Shutting down webadmin")
    webadmin.foreach(_.stop())
    Logger.debug("Shutting down neo4j")
    graphdb.foreach(_.shutdown())
  }

}
