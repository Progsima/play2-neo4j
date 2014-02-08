package models

import com.logisima.play.neo4j.Neo4j

import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._

import scala.concurrent.Future

/**
 * Definition of a ContentType object.
 *
 * @param name
 * @param schema
 */
case class ContentType(name :String, schema :String)

/**
 * Helper for model ContentType.
 *
 * @author : bsimard
 */
object ContentType {

  /**
   * List of cypher queries
   */
  private val getQuery :String = "MATCH (n:Content_Type { name: {name} }) RETURN n;"
  private val createQuery :String = "CREATE (n:Content_Type {params}) RETURN n;"
  private val updateQuery :String = "MATCH (n:Content_Type { name: {name} }) SET n = {params} RETURN n"
  private val deleteQuery :String = "MATCH (n:Content_Type { name: {name} }) DELETE n; "
  private val listQuery :String = "MATCH (n:Content_Type) RETURN n SKIP {skip} LIMIT {limit}"

  implicit val contentTypeReads = Json.reads[ContentType]
  implicit val contentTypeWrites = Json.writes[ContentType]

  /**
   * Retrieve a contentType by its name.
   *
   * @param name
   */
  def get( name :String) :Future[Option[ContentType]] = {
    for ( jsonResultSet <- Neo4j.cypher(getQuery, Map("name" -> name))) yield {
      if(jsonResultSet.size > 0) {
        Some(jsonResultSet(0).as[ContentType])
      }
      else{
        None
      }
    }
  }

  /**
   * Method to create a contentType.
   *
   * @param json
   */
  def create( json :JsValue) :Future[Option[ContentType]] = {
    for ( jsonResultSet <- Neo4j.cypher(createQuery, Map("name" -> json \ "name", "schema" -> json \ "schema"))) yield {
      if(jsonResultSet.size > 0) {
        Some(jsonResultSet(0).as[ContentType])
      }
      else{
        None
      }
    }
  }

  /**
   *
   * @param json
   */
  def update( json :JsValue ) {
    for ( jsonResultSet <- Neo4j.cypher(updateQuery, Map("name" -> json \ "name", "schema" -> json \ "schema"))) yield {
      if(jsonResultSet.size > 0) {
        Some(jsonResultSet(0).as[ContentType])
      }
      else{
        None
      }
    }
  }

  /**
   * Method to delete a contentType by its name.
   *
   * @param name
   */
  def delete(name :String) :Future[Boolean] = {
     for ( jsonResultSet <- Neo4j.cypher(deleteQuery, Map("name" -> name))) yield {
       true
     }
  }

  /**
   *
   * @param skip
   * @param limit
   */
  def list(skip :Int = 0, limit :Int = 10) :Future[Seq[ContentType]] = {
    for ( jsonResultSet <- Neo4j.cypher(listQuery, Map("skip" -> skip, "limit" -> limit)) ) yield {
      jsonResultSet.map {
        jsValue =>
          jsValue.as[ContentType]
      }
    }
  }

}
