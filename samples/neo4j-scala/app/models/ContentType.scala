package models

import com.logisima.play.neo4j.Neo4j

import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import com.wordnik.swagger.annotations._
import scala.annotation.meta.field

/**
 * Definition of a ContentType object.
 *
 * @param name Name of the content type
 * @param schema Json schema of the content type
 * @param description Description of the content type
 */
@ApiModel("ContentType")
case class ContentType(
  @(ApiModelProperty @field)(position=1, required=true) name :String,
  @(ApiModelProperty @field)(position=2, required=true) schema :String,
  @(ApiModelProperty @field)(position=3) description :Option[String]
)

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
  private val createQuery :String = "CREATE (n:Content_Type) SET n.name={name}, n.schema={schema}, n.description={description} RETURN n;"
  private val updateQuery :String = "MATCH (n:Content_Type { name: {id} }) SET n.schema={name}, n.schema={schema}, n.description={description} RETURN n"
  private val deleteQuery :String = "MATCH (n:Content_Type { name: {name} }) DELETE n; "
  private def deleteAllContentQuery(contentType :String) = s"MATCH (n:$contentType) DELETE n;"
  private val listQuery :String = "MATCH (n:Content_Type) RETURN n SKIP {skip} LIMIT {limit}"

  implicit val contentTypeReads = Json.reads[ContentType]
  implicit val contentTypeWrites = Json.writes[ContentType]

  /**
   * Method to get all ContentType.
   *
   * @param skip The starting position at wich we retrieve data
   * @param limit Number of data to return
   */
  def list(skip :Int = 0, limit :Int = 10) :Future[Seq[ContentType]] = {
    for ( jsonResultSet <- Neo4j.cypher(listQuery, Map("skip" -> skip, "limit" -> limit)) ) yield {
      jsonResultSet.map {
        jsValue =>
          jsValue.as[ContentType]
      }
    }
  }

  /**
   * Method to retrieve a contentType by its name.
   *
   * @param name Name of the content type
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
   * @param json Json data of the content type
   */
  def create( json :JsValue) :Future[Option[ContentType]] = {
    for (
      jsonResultSet <- Neo4j.cypher(
        createQuery,
        Map(
          "name" -> (json \ "name").as[String],
          "schema" -> (json \ "schema").as[String],
          "description" -> (json \ "description").asOpt[String].getOrElse(JsNull)
        )
      )) yield {

      if(jsonResultSet.size > 0) {
        Some(jsonResultSet(0).as[ContentType])
      }
      else{
        None
      }
    }
  }

  /**
   * Method to update a ContentType.
   *
   * @param json Json data of the content type
   */
  def update( name: String, json :JsValue ) :Future[Option[ContentType]] = {
    for (
      jsonResultSet <- Neo4j.cypher(
        updateQuery,
        Map(
          "id" -> name,
          "name" -> (json \ "name").as[String],
          "schema" -> (json \ "schema").as[String],
          "description" -> (json \ "description").asOpt[String].getOrElse("")
        ))) yield {
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
   * /!\ We also delete all content of the type.
   *
   * @param name Name of the content type
   */
  def delete(name :String) :Future[Boolean] = {
    val queries :Array[(String, Map[String, _])] = Array((deleteQuery, Map("name" -> name)), (deleteAllContentQuery(name),Map[String, Any]()))
     for ( jsonResultSet <- Neo4j.cypher(queries)) yield {
       true
     }
  }

  /**
   * Method to retrieve the json schema of the content Type.
   *
   * @param name Name of the content type
   * @return
   */
  def schema(name :String) :Future[Option[String]] = {
    for ( jsonResultSet <- Neo4j.cypher(getQuery, Map("name" -> name))) yield {
      if(jsonResultSet.size > 0) {
        val schema = jsonResultSet(0).\("schema").toString().replace("\\\"", "\"")
        Some(schema.substring(1, schema.length -1))
      }
      else{
        None
      }
    }
  }

}
