package controllers

import play.api._
import play.api.mvc._
import org.neo4j.graphdb.{Node, Transaction}
import services.Neo4j

object Application extends Controller {

  def index = Action {
    val tx: Transaction = Neo4j.graphDb.beginTx()
    try {
      val node: Node = Neo4j.graphDb.createNode()
      node.setProperty("test", "test")
      tx.success()
    } catch {
      case e: Exception => Logger.error("Error when create node", e)
    } finally {
      tx.finish()
    }
    Ok(views.html.index("Your new application is ready."))
  }

}