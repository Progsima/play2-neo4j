package controllers

import scala.util.control.Exception._

import play.api._
import play.api.mvc._

import org.neo4j.graphdb.{GraphDatabaseService, Node, Transaction}
import services.Neo4j


object Application extends Controller {

  def transactional[B](f: (GraphDatabaseService, Transaction) => B):Option[B] = {
    //create the transaction => no exception here?
    val tx =  for {
                db   <- Neo4j.graphDb 
                tx   <- Some(db.beginTx())
              } yield tx

    //execute the code => handling the exception wrapped in an Option
    val b = catching(classOf[Exception]) {
      for {
        db  <- Neo4j.graphDb 
        t   <- tx
        b   <- Some(f(db, t))
      } yield b
    }

    // if the transaction exists => close it
    tx.foreach(_.finish())

    //return the result lifted in Option
    b
  }

  def index = Action {
    val node = transactional { (db, t) =>
      val node = db.createNode()
      node.setProperty("test", "test")
      node
    }

    node match {
      case None => InternalServerError("Unable to create the node")
      case Some(x) => Ok(views.html.index(s"Node just created : ${x.getId()}"))
    }
  }

}