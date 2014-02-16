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
import com.logisima.play.neo4j.service.Neo4jTransactionalService
import scala.concurrent.Future
import play.api.libs.json.JsValue

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
  var serverUrl: String = "http://localhost:" + Play.configuration.getInt("neo4j.embedded.port").getOrElse(7575)

  /**
   * Starting neo4j database.
   *
   * @return
   */
  private def startDb() = {
    val DBPath: String = Play.configuration.getString("neo4j.embedded.path").getOrElse("neo4j")
    Logger.debug("[Neo4j]: Database path is : " + DBPath)

    // Create the neo4j database
    val graphdb = FileUtils.getFile(neo4jPropertiesPath) match {
      case Some(f: File) =>
        new GraphDatabaseFactory()
          .newEmbeddedDatabaseBuilder(DBPath)
          .loadPropertiesFromFile(neo4jPropertiesPath)
          .newGraphDatabase()
          .asInstanceOf[GraphDatabaseAPI]
      case None =>
        new GraphDatabaseFactory()
          .newEmbeddedDatabaseBuilder(DBPath)
          .setConfig(ShellSettings.remote_shell_enabled, Settings.TRUE)
          .newGraphDatabase()
          .asInstanceOf[GraphDatabaseAPI]
    }

    // create the neo4j server
    val config = new ServerConfigurator(graphdb)
    // let the server endpoint be on a custom port
    config.configuration().setProperty(
      Configurator.WEBSERVER_PORT_PROPERTY_KEY,
      Play.configuration.getInt("neo4j.embedded.port").getOrElse(7575)
    )

    val srv = new WrappingNeoServerBootstrapper(graphdb, config)

    (graphdb, srv)
  }

  /**
   * Starting webadmin server.
   *
   * @return
   */
  private def startAdmin() = {
    Logger.debug("[Neo4j]: Starting webadmin")
    webadmin.map {
      server => server.start()
    }
  }

  /**
   * Start the database with primary nodes.
   */
  def start() {
    Logger.debug("[Neo4j]: Init database")

    Play.configuration.getString("neo4j.url") match {

      // Configure for a distant neo4j server
      case Some(url: String) => serverUrl = url

      // Configure and start an embedded server
      case None =>
        Some(startDb()).map {
          case (x, y) =>
            graphdb = Some(x)
            webadmin = Some(y)
        }
        startAdmin()
    }
  }

  /**
   * Stopping server & admin server
   */
  def stop() {
    Logger.debug("[Neo4j]: Shutting down webadmin")
    webadmin.foreach(_.stop())
    Logger.debug("[Neo4j]: Shutting down neo4j")
    graphdb.foreach(_.shutdown())
  }

  /**
   * Start a transaction, and return its id.
   *
   * @return
   */
  def beginTx() :Future[Int] = {
     new Neo4jTransactionalService(this.serverUrl).beginTx()
  }

  /**
   * Commit a transaction by its id.
   *
   * @param transId Identifier of the transaction to commit
   * @return
   */
  def commit(transId :Int) :Future[Boolean] = {
    new Neo4jTransactionalService(this.serverUrl).commit(transId)
  }

  /**
   * Rollback a transaction by its id.
   *
   * @param transId Identifier of the transaction to rollback
   * @return
   */
  def rollback(transId :Int) :Future[Boolean] = {
    new Neo4jTransactionalService(this.serverUrl).rollBack(transId)
  }

  /**
   * Execute a single cypher query and commit it.
   *
   * @param query Cypher query
   * @return
   */
  def cypher(query: String) :Future[Seq[JsValue]] = {
    new Neo4jTransactionalService(this.serverUrl).doSingleCypherQuery(query, Map[String, Any](), None)
  }

  /**
   * Execute a single cypher query with params and commit it.
   *
   * @param query Cypher query
   * @param params Parameters of the cypher query
   * @return
   */
  def cypher(query: String,  params: Map[String, _]) :Future[Seq[JsValue]] = {
    new Neo4jTransactionalService(this.serverUrl).doSingleCypherQuery(query, params, None)
  }

  /**
   * Exceute a set of cypher query with params into the same transaction, and commit it.
   *
   * @param queries Array of cypher query with their parameters
   * @return
   */
  def cypher(queries: Array[(String, Map[String, _])]): Future[Array[Seq[JsValue]]] = {
    new Neo4jTransactionalService(this.serverUrl).doCypherQuery(queries, None)
  }

  /**
   * Execute a single cypher query into the specified transaction.
   *
   * @param query Cypher query
   * @param transactionId Identifier of transaction that will handle the cypher query
   * @return
   */
  def cypher(query: String, transactionId :Int) :Future[Seq[JsValue]] = {
    new Neo4jTransactionalService(this.serverUrl).doSingleCypherQuery(query, Map[String, Any](), Some(transactionId))
  }

  /**
   * Execute a single cypher query with params into the specified transaction.
   *
   * @param query  Cypher query
   * @param params Parameters of the cypher query
   * @param transactionId Identifier of transaction that will handle the cypher query
   * @return
   */
  def cypher(query: String, params: Map[String, _], transactionId :Int) :Future[Seq[JsValue]] = {
    new Neo4jTransactionalService(this.serverUrl).doSingleCypherQuery(query, params, Some(transactionId))
  }

  /**
   * Execute a set of cypher query with params into the specified transaction.
   *
   * @param queries Array of cypher query with their parameters
   * @param transactionId Identifier of transaction that will handle the cypher query
   * @return
   */
  def cypher(queries: Array[(String, Map[String, Any])], transactionId :Int): Future[Array[Seq[JsValue]]] = {
    new Neo4jTransactionalService(this.serverUrl).doCypherQuery(queries, Some(transactionId))
  }

}
