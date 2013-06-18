package services

import play.api.Play
import play.Logger
import play.api.Play.current
import org.neo4j.graphdb._
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.kernel.GraphDatabaseAPI
import org.neo4j.server.WrappingNeoServerBootstrapper

/**
 * Neo4j database object.
 *
 * @author : bsimard
 */
object Neo4j {

  /**
   * Graph database
   */
  var graphDb :Option[GraphDatabaseService] = None

  /**
   * Server for webadmin
   */
  var webadmin :Option[WrappingNeoServerBootstrapper] = None

  /**
   * Starting neo4j database
   *
   * @return
   */
  def start() :GraphDatabaseService = {
    val DBPath :String = Play.configuration.getString("neo4j.path").getOrElse("neo4j");
    Logger.debug("Neo4j database path is :" + DBPath);
    new GraphDatabaseFactory().newEmbeddedDatabase(DBPath)
  }

  /**
   * Starting webadmin server
   *
   * @return
   */
  def startAdmin() : Option[WrappingNeoServerBootstrapper] = {
    Logger.debug("Starting webadmin")
    graphDb.map { db =>
      val bootstrapper =  new WrappingNeoServerBootstrapper(db.asInstanceOf[GraphDatabaseAPI])
      bootstrapper.start()
      bootstrapper
    }
  }

  /**
   * Init the database with primary nodes
   */
  def initDb() {
    Logger.debug("init database")
    graphDb = Some(start())
    webadmin = startAdmin()
  }

  /**
   * Stopping server & admin server
   */
  def stop() {
    Logger.debug("Suhting done webadmin")
    webadmin.foreach(_.stop())
    Logger.debug("Suhting done neo4j")
    graphDb.foreach(_.shutdown())
  }

}
