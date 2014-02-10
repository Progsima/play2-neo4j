package tools

import com.logisima.play.neo4j.evolution.{EvolutionFeatureMode, Evolution}
import com.logisima.play.neo4j.Neo4j
import com.logisima.play.neo4j.service.Neo4jEvolutionService
import com.logisima.play.neo4j.utils.{Neo4jUtils, FileUtils}
import java.io.File
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.Play
import scala.io.Source

/**
 * Spec to test Json tools.
 */
class JsonToolsSpec extends Specification {

  "JsonTools" should {

    "validate a JSON + schema and return errors" in {
        val schema :String = """{
                                   "$schema": "http://json-schema.org/draft-04/schema#",
                                   "title": "Product",
                                   "description": "A product from Acme's catalog",
                                   "type": "object",
                                   "properties": {
                                       "id": {
                                           "description": "The unique identifier for a product",
                                           "type": "integer"
                                       },
                                       "name": {
                                           "description": "Name of the product",
                                           "type": "string"
                                       },
                                       "price": {
                                           "type": "number",
                                           "minimum": 0,
                                           "exclusiveMinimum": true
                                       },
                                       "tags": {
                                           "type": "array",
                                           "items": {
                                               "type": "string"
                                           },
                                           "minItems": 1,
                                           "uniqueItems": true
                                       }
                                   },
                                   "required": ["id", "name", "price"]
                               }"""
      val json :String = """{ "id": "test", "name":"test", "price":"12"}"""
      val errors = JsonTools.validate(schema, json)

      errors should beEqualTo(Some(List("instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])", "instance type (string) does not match any allowed primitive type (allowed: [\"integer\",\"number\"])")))
    }

  }
}