package models

import scala.concurrent.Future
import com.logisima.play.neo4j.Neo4j
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.concurrent.Execution.Implicits._
import tools.JsonTools

/**
 * Helper for model Content.
 *
 * @author : bsimard
 */
object Content {

  /**
   * List of cypher queries
   */
  private def getQuery(contentType :String) = s"MATCH (n:${contentType} { uuid:{uuid} }) RETURN n;"
  private def createQuery(contentType :String) = s"CREATE (n:${contentType} {params}) RETURN n;"
  private def updateQuery(contentType :String) = s"MATCH (n:${contentType} { uuid:{uuid} }) SET {params} RETURN n"
  private def deleteQuery(contentType :String) = s"MATCH (n:${contentType} { uuid:{uuid} }) DELETE n; "
  private def listQuery(contentType :String) = s"MATCH (n:${contentType}) RETURN n SKIP {skip} LIMIT {limit}"

  /**
   * List all content of a type.
   *
   * @param skip
   * @param limit
   */
  def list(contentType: String, skip: Int = 0, limit: Int = 10): Future[Seq[ContentType]] = {
    for (jsonResultSet <- Neo4j.cypher(listQuery(contentType), Map("skip" -> skip, "limit" -> limit))) yield {
      jsonResultSet.map {
        jsValue =>
          jsValue.as[ContentType]
      }
    }
  }

  /**
   * Retrieve a content by its uuid and contentType.
   *
   * @param contentType
   * @param uuid
   */
  def get(contentType: String, uuid: String): Future[Option[JsValue]] = {
    for (jsonResultSet <- Neo4j.cypher(getQuery(contentType), Map("uuid" -> uuid))) yield {
      if (jsonResultSet.size > 0) {
        Some(jsonResultSet(0))
      }
      else {
        None
      }
    }
  }

  /**
   * Method to create a content.
   *
   * @param contentType
   * @param json
   */
  def create(contentType: String, json: JsValue): Future[Option[JsObject]] = {
    ContentType.schema(contentType).flatMap{ optionSchema =>
      optionSchema match {
        case Some(schema: String) => {
          JsonTools.validate(schema, json.toString) match {
            case Some(errors) => Future { None }// TODO Exception or Either ?
            case None => {
              for (
                jsonResultSet <- Neo4j.cypher(
                  createQuery(contentType),
                  Map(
                    "params" -> json.as[JsObject]
                  ))) yield {

                if (jsonResultSet.size > 0) {
                  Some(jsonResultSet(0).as[JsObject])
                }
                else {
                  None
                }
              }
            }
          }
        }
        case None => Future { None }// TODO Exception or Either ?
      }
    }
  }

  /**
   * Update a content by its type & uuid.
   *
   * @param contentType
   * @param uuid
   * @param json
   */
  def update(contentType: String, uuid: String, json: JsValue): Future[Option[JsObject]] = {
    ContentType.schema(contentType).flatMap{ optionSchema =>
      optionSchema match {
        case Some(schema: String) => {
          JsonTools.validate(schema, json.toString) match {
            case Some(errors) => Future { None }// TODO Exception or Either ?
            case None => {
              for (
                jsonResultSet <- Neo4j.cypher(
                  updateQuery(contentType),
                  Map(
                    "uuid" -> uuid,
                    "params" -> json.as[JsObject]
                  ))) yield {

                if (jsonResultSet.size > 0) {
                  Some(jsonResultSet(0).as[JsObject])
                }
                else {
                  None
                }
              }
            }
          }
        }
        case None => Future { None }// TODO Exception or Either ?
      }
    }
  }

  /**
   * Method to delete a contentType by its name.
   *
   * @param contentType
   * @param uuid
   */
  def delete(contentType: String, uuid: String): Future[Boolean] = {
    for (jsonResultSet <- Neo4j.cypher(deleteQuery(contentType), Map("uuid" -> uuid))) yield {
      true
    }
  }

}
