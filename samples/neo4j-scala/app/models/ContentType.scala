package models

import play.api.libs.json.JsValue
import com.logisima.play.neo4j.Neo4j

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
  val getQuery :String = "MATCH (n:Content_Type { name: {name} }) RETURN n;"
  val createQuery :String = "CREATE (n:Content_Type {params}) RETURN n;"
  val updateQuery :String = "MATCH (n:Content_Type { name: {name} }) SET n = {params} RETURN n"
  val deleteQuery :String = "MATCH (n:Content_Type { name: {name} }) DELETE n; "
  val listQuery :String = "MATCH (n:Content_Type) RETURN n SKIP {skip} LIMIT {limit}"

  /**
   * Retrieve a contentType by its name.
   *
   * @param name
   */
  def get( name :String) {
    Neo4j.cypher(getQuery, Map("name" -> name))
  }

  /**
   * Method to create a contentType.
   *
   * @param json
   */
  def create( json :JsValue) {
  }

  /**
   *
   * @param json
   */
  def update( json :JsValue ) {
  }

  /**
   * Method to delete a contentType by its name.
   *
   * @param name
   */
  def delete(name :String) {

  }

  /**
   *
   * @param skip
   * @param limit
   */
  def list(skip :Int, limit :Int) {
  }

}
