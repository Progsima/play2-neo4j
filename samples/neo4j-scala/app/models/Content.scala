package models

import scala.concurrent.Future
import com.logisima.play.neo4j.Neo4j
import play.api.libs.json.{Json, JsObject, JsValue}
import play.api.libs.concurrent.Execution.Implicits._
import tools.JsonTools
import java.util.UUID
import com.logisima.play.neo4j.exception.Neo4jException
import play.Logger


/**
 * Helper for model Content.
 *
 * @author : bsimard
 */
object Content {

  /**
   * List of cypher queries
   */
  private def getQuery(contentType :String) = s"MATCH (n:$contentType { uuid:{uuid} }) RETURN n;"
  private def createQuery(contentType :String) = s"CREATE (n:$contentType) SET n= {params} RETURN n;"
  private def updateQuery(contentType :String) = s"MATCH (n:$contentType { uuid:{uuid} }) SET n = {params} RETURN n"
  private def deleteQuery(contentType :String) = s"MATCH (n:$contentType { uuid:{uuid} }) DELETE n;"
  private def listQuery(contentType :String, sorting :String) = s"MATCH (n:$contentType) RETURN n ORDER BY $sorting SKIP {skip} LIMIT {limit};"
  private def countAllQuery(contentType :String) = s"MATCH (n:$contentType) RETURN count(*)"

  /**
   * List all content of a type.
   */
  def list(contentType :String, page :Int, row :Int, sort :String, order :String, filter :String) :Future[(Int, Seq[JsValue])] = {

    val skip :Int = (page-1) * row;

    val queries = Array(
        ( countAllQuery( contentType ), Map[String, Any]() ),
        ( listQuery( contentType, "n." + sort + " " + order ), Map( "skip" -> skip, "limit" -> row ) )
    )

    for ( neoResultSet <- Neo4j.cypher(queries) ) yield {
      val totalRows = neoResultSet.apply(0).apply(0).as[Int]
      val datas = neoResultSet.apply(1)
      (totalRows, datas)
    }

  }

  /**
   * Retrieve a content by its uuid and contentType.
   *
   * @param contentType Type of the content
   * @param uuid the identifier of the content
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
   * @param contentType Type of the content
   * @param json Json data of the content
   */
  def create(contentType: String, json: JsValue): Future[Option[JsObject]] = {
    ContentType.schema(contentType).flatMap {
      case Some(schema: String) =>
        JsonTools.validate(schema, json.toString()) match {
          case Some(errors) => throw new Neo4jException("JSON SCHEMA VALIDATION", "Json schema validation failed with following errors " + Json.toJson(errors))
          case None =>
            val params: JsObject = json.as[JsObject].+(("uuid", Json.toJson(UUID.randomUUID().toString)))
            for (
              jsonResultSet <- Neo4j.cypher(
                createQuery(contentType),
                Map(
                  "params" -> params
                ))) yield {

              if (jsonResultSet.size > 0) {
                Some(jsonResultSet(0).as[JsObject])
              }
              else {
                None
              }
            }
        }
      case None => throw new Neo4jException("BAD CONTENT TYPE", "JSON Schema not found for content type " + contentType)
    }
  }

  /**
   * Update a content by its type & uuid.
   *
   * @param contentType Type of the content
   * @param uuid The identifier of the content
   * @param json Json data of the content
   */
  def update(contentType: String, uuid: String, json: JsValue): Future[Option[JsObject]] = {
    ContentType.schema(contentType).flatMap {
      case Some(schema: String) =>
        JsonTools.validate(schema, json.toString()) match {
          case Some(errors) => throw new Neo4jException("JSON SCHEMA VALIDATION", "Json schema validation failed with following errors " + Json.toJson(errors))
          case None =>
            val params: JsObject = json.as[JsObject].+(("uuid", Json.toJson(uuid)))
            for (
              jsonResultSet <- Neo4j.cypher(
                updateQuery(contentType),
                Map(
                  "uuid" -> uuid,
                  "params" -> params
                ))) yield {

              if (jsonResultSet.size > 0) {
                Some(jsonResultSet(0).as[JsObject])
              }
              else {
                None
              }
            }
        }
      case None => throw new Neo4jException("BAD CONTENT TYPE", "JSON Schema not found for content type " + contentType)
    }
  }

  /**
   * Method to delete a contentType by its name.
   *
   * @param contentType Type of the content
   * @param uuid The identifier of the content
   */
  def delete(contentType: String, uuid: String): Future[Boolean] = {
    for (jsonResultSet <- Neo4j.cypher(deleteQuery(contentType), Map("uuid" -> uuid))) yield {
      true
    }
  }

}
