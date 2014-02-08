package com.logisima.play.neo4j.service

import com.logisima.play.neo4j.evolution._
import com.logisima.play.neo4j.evolution.CypherScriptType.CypherScriptType
import com.logisima.play.neo4j.evolution.EvolutionFeatureMode.EvolutionFeatureMode
import com.logisima.play.neo4j.utils.FileUtils
import com.logisima.play.neo4j.Neo4j
import play.{Logger, Play}
import play.api.libs.Collections
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import java.io.File
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.io.Source
import scala.Some
import com.logisima.play.neo4j.exception.Neo4jInvalidRevision

/**
 * Neo4j service that handle evolution script.
 *
 * @author : bsimard
 */
object Neo4jEvolutionService {

  /**
   * String interpolation to construct evolution script relative path.
   *
   * @param revision
   * @return
   */
  private def evolutionsFilename(path: String, revision: Int, style: CypherScriptType): String = s"${path}/conf/evolutions/neo4j/${revision}_${style}.cql"

  /**
   * String interpolation to construct cypher create query for evolution
   *
   * @param revision
   * @return
   */
  private def cypherEvolutionQuery(revision: Int, cypher_down: String, cypher_up: String): String = s"""CREATE (n:Play_Evolutions { revision:${revision}, cypher_down:"${cypher_down}", cypher_up:"${cypher_up}" });"""

  /**
   * Retrieve evolutions from neo4j database that have been apply.
   * NB: this is a blocking call
   *
   * @return
   */
  def neo4jEvolutions(): Future[Seq[Evolution]] = {

    val query: String = "MATCH (n:Play_Evolutions) RETURN n ORDER BY n.revision"
    val result: Future[Seq[JsValue]] = Neo4j.cypher(query)

    for(datas <- result) yield {
      Logger.debug("[Evolution]: Neo4j evolutions is " + datas)
      implicit val evolutionsReads = Json.reads[Evolution]
      datas.map(
        jsValue => {
          jsValue.validate[Evolution].asOpt.get
        }
      )
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
        FileUtils.getFile(evolutionsFilename(Play.application().path().getPath, revision, CypherScriptType.up)) match {
          case Some(file: File) => {
            val upScript: String = Source.fromFile(file).getLines() mkString "\n"
            val downScript: String = FileUtils.getFile(evolutionsFilename(Play.application().path().getPath, revision, CypherScriptType.down)) match {
              case Some(file: File) => Source.fromFile(file).getLines() mkString "\n"
              case _ => ""
            }
            Option((revision + 1, Evolution(revision, upScript, downScript)))
          }
          case None => None
        }
      }
    }.reverse
  }

  /**
   * Apply the given cypher evolution script.
   *
   * @param script
   */
  def applyScript(script: String)  = {
    // create list of queries for the evolution
    val queries: Seq[String] = statements(script)
    // here we add/remove the evolution node into database
    val params: Array[(String, Map[String, Any])] = queries.map {
      query =>
        (query, Map[String, Any]())
    }.toArray
    (Await result(Neo4j.cypher(params), Duration.Inf))
  }

  /**
   * The cypher script separated into constituent ";"-delimited statements.
   *
   * @see play source code play.api.db.evolutions
   * @param cql the cypher script
   */
  def statements(cql: String): Seq[String] = {
    // Regex matches on semicolons that neither precede nor follow other semicolons
    cql.replaceAll("^//.*$", "").split("(?<!;);(?!;)").map(_.trim.replace(";;", ";")).filter(_ != "")
  }

  /**
   * Check the evolution of the application, and apply script if necessary.
   *
   * @param mode of the evolution, can be enable, disable or auto.
   * @return
   */
  def checkEvolutionState(mode: EvolutionFeatureMode) = {
    val appEvol = applicationEvolutions()
    val neo4jEvol = Await result(neo4jEvolutions(),  Duration.Inf)

    val downEvolution: Seq[Evolution] = neo4jEvol.diff(appEvol)
    val upEvolution: Seq[Evolution] = appEvol.diff(neo4jEvol)
    Logger.debug("[Evolution]: down evolutions are " + downEvolution)
    Logger.debug("[Evolution]: up evolutions are " + upEvolution)

    if (downEvolution.size > 0 | upEvolution.size > 0) {

      // render / action of the check
      mode match {
        case EvolutionFeatureMode.auto => {
          // let's go to send all up script one by one to neo4j.
          // each apply is a transaction, so a script can handle schema modification
          downEvolution.map {
            evolution  =>
              applyScript(evolution.cypher_down)
              applyScript("MATCH (n:Play_Evolutions) WHERE n.revision=" + evolution.revision + " DELETE n;")
          }
          upEvolution.map {
            evolution  =>
              applyScript(evolution.cypher_up)
              applyScript(
                  cypherEvolutionQuery(
                    evolution.revision,
                    // here we escape quote for up & down script and replace ; to ;; for the statement function
                    evolution.cypher_down.replace("\"", "\\\"") replace(";", ";;"),
                    evolution.cypher_up.replace("\"", "\\\"").replace(";", ";;")
                  )
              )
          }

        }
        case _ => {
          // we throw an exception to ask the user
          val down = downEvolution.foldLeft("Down \n\n") {
            (message, evolution) => message + evolution.cypher_down + "\n\n"
          }
          val up  = upEvolution.foldLeft("Up \n\n") {
            (message, evolution) => message + evolution.cypher_up + "\n\n"
          }
          val message =
          throw new Neo4jInvalidRevision(down + up)
        }
      }

    }
  }

}