package com.logisima.play.neo4j.service

import play.api.libs.Collections
import play.{Logger, Play}
import com.logisima.play.neo4j.utils.FileUtils
import java.io.File
import scala.io.Source
import com.logisima.play.neo4j.evolution.{ScriptType, Evolution}
import com.logisima.play.neo4j.evolution.ScriptType.ScriptType
import com.logisima.play.neo4j.exception.Neo4jException
import play.api.libs.json.{Json, JsValue}
import com.logisima.play.neo4j.Neo4j
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
 * Neo4j service that handle evolution script.
 *
 * @author : bsimard
 */
class Neo4jEvolutionService(rootUrl: String) {

  /**
   * String interpolation to construct evolution script relative path.
   *
   * @param revision
   * @return
   */
  private def evolutionsFilename(path: String, revision: Int, style: ScriptType): String = s"${path}/conf/evolutions/neo4j/${revision}_${style}.cql"

  /**
   * String interpolation to construct cypher create query for evolution
   *
   * @param revision
   * @return
   */
  private def cypherEvolutionQuery(revision: Int, cypher_down: String, cypher_up: String): String = s"""CREATE (n:Play_evolution { revision:${revision}, cypher_down:"${cypher_down}", cypher_up:"${cypher_up}" });"""

  /**
   * Retrieve evolutions from neo4j database that have been apply.
   * NB: this is a blocking call
   *
   * @return
   */
  def neo4jEvolutions(): Seq[Evolution] = {

    val query: String = "MATCH (n:Play_Evolutions) RETURN n ORDER BY n.revision"
    val result: Future[Either[Neo4jException, Seq[JsValue]]] = new Neo4jTransactionalService(Neo4j.serverUrl).cypher(query)

    val response = Await result(result, 2 seconds)
    Logger.debug("Neo4j evolutions is " + response)

    response match {
      case Left(x) => Seq.apply()
      case Right(datas: Seq[JsValue]) => {
        Logger.debug("Datas for neo4j evolutions are " + datas)
        implicit val evolutionsReads = Json.reads[Evolution]
        datas.map(
          jsValue => {
            Logger.debug("Element of datas " + datas)
            jsValue(0).validate[Evolution].asOpt.get
          }
        )
      }
      case _ => Seq.apply()
    }
  }

  /**
   * Retrieve evolutions from the application.
   *
   * @return
   */
  def applicationEvolutions(): Seq[Evolution] = {
    Collections.unfoldLeft(1) {
      revision => {
        FileUtils.getFile(evolutionsFilename(Play.application().path().getPath, revision, ScriptType.up)) match {
          case Some(file: File) => {
            val upScript: String = Source.fromFile(file).getLines() mkString "\n"
            val downScript: String = FileUtils.getFile(evolutionsFilename(Play.application().path().getPath, revision, ScriptType.down)) match {
              case Some(file: File) => Source.fromFile(file).getLines() mkString "\n"
              case _ => ""
            }
            Option((revision + 1, Evolution(revision, upScript, downScript)))
          }
          case None => None
        }
      }
    }
  }

  /**
   * Apply the given cypher evolution script.
   *
   * @param script
   */
  def applyScript(script: String): Either[Neo4jException, Array[Seq[JsValue]]] = {
    // create list of queries for the evolution
    val queries: Seq[String] = statements(script)
    // here we add/remove the evolution node into database
    val params: Array[(String, Map[String, Any])] = queries.map {
      query =>
        (query, Map[String, Any]())
    }.toArray
    Await result(new Neo4jTransactionalService(Neo4j.serverUrl).cypher(params), 2 seconds)
  }

  /**
   * The cypher script separated into constituent ";"-delimited statements.
   *
   * @see play source code play.api.db.evolutions
   */
  def statements(cql: String): Seq[String] = {
    // Regex matches on semicolons that neither precede nor follow other semicolons
    cql.split("(?<!;);(?!;)").map(_.trim.replace(";;", ";")).filter(_ != "")
  }

  /**
   * Check the evolution of the application, and apply script if necessary.
   *
   * @return
   */
  def checkEvolutionState() = {
    val appEvol = applicationEvolutions()
    val neo4jEvol = neo4jEvolutions()

    Logger.debug("Application evolution is " + appEvol)
    Logger.debug("Neo4j evolution is " + neo4jEvol)

    // check for down script
    var downScript = ""
    var startingUpStep = 0
    if (neo4jEvol.size > 0) {
      for (i <- (neo4jEvol.size - 1) to 0) {
        // here I assume that app evol is higer than database evol...
        if (neo4jEvol(i).hash != appEvol(i).hash) {
          downScript += "\n" + neo4jEvol(i).cypher_down
          startingUpStep = i
        }
      }
    }
    Logger.debug("Down script is " + downScript)

    // let's do up script
    var playEvolutionScript: String = "MATCH (n:Play_Evolutions) DELETE n;"
    var upScript :String = ""
    if (appEvol.size > 0) {
      for (i <- startingUpStep to (appEvol.size - 1)) {
        upScript += "\n" + appEvol(i).cypher_up
      }
      Logger.debug("Up script is " + upScript)

      // let's update the db with all application script
      for (i <- 0 to (appEvol.size-1)) {
        val evol = appEvol(i)
        // here we escape quote for up & down script and replace ; to ;; for the statement function
        val cypherQuery = cypherEvolutionQuery(evol.revision, evol.cypher_down.replace("\"", "\\\"")replace(";", ";;"), evol.cypher_up.replace("\"", "\\\"").replace(";", ";;"))
        Logger.debug("Cypher query is " + cypherQuery)
        playEvolutionScript += cypherQuery
      }
    }

    // let's go to send all script to neo4j
    val allScript = downScript + upScript + playEvolutionScript
    val result = applyScript(allScript)
  }

}