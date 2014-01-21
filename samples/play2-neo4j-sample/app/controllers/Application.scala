package controllers

import scala.util.control.Exception._

import play.api.mvc._

import org.neo4j.graphdb.{GraphDatabaseService,Transaction}
import services._


object Application extends Controller {

  def transactional[B](f: (GraphDatabaseService, Transaction) => B):Option[B] = {
    //create the transaction => no exception here?
    val tx =  for {
                db   <- Neo4j.graphdb
                tx   <- Some(db.beginTx())
              } yield tx

    //execute the code => handling the exception wrapped in an Option
    val b = catching(classOf[Exception]) {
      for {
        db  <- Neo4j.graphdb
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
      t.success()
      node
    }

    node match {
      case None => InternalServerError("Unable to create the node")
      case Some(x) => Ok(views.html.index(s"Node just created : ${x.getId()}"))
    }
  }

}