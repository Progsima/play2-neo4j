package controllers

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import play.api.libs.json._
import com.logisima.play.neo4j.utils.Neo4jUtils

/**
 * Spec to test Content Type controller of the sample application.
 */
class ContentTypesSpec extends Specification {

  "API Content type" should {

    "list content type" in {
      running(FakeApplication()) {
        // Reset the entire database
        Neo4jUtils.reset()

        val Some(result) = route(FakeRequest(GET, "/api/types"))

        // testing response headers
        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")

        // testing response body
        contentAsJson(result).as[JsArray].value.size mustEqual(2)
      }
    }

    "get a content type" in {
      running(FakeApplication()) {
        // Reset the entire database
        Neo4jUtils.reset()

        val Some(result) = route(FakeRequest(GET, "/api/types/Tag"))

        // testing response headers
        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")

        // testing response body
        contentAsString(result) must beEqualTo("""{"name":"Tag","description":"Type that represent tag","schema":"{ \"title\": \"Tag Content Type\", \"type\": \"object\",\"properties\": {\"name\": {\"type\": \"string\"},\"description\": {\"type\": \"string\"}},\"required\": [\"name\"] }"}""")
      }
    }

    "create a content type without description" in {
      running(FakeApplication()) {
        // delete all
        Neo4jUtils.deleteAll()

        val Some(result) = route(FakeRequest(POST, "/api/types").withBody(
          Json.obj(
            "name" -> "MyNewType",
            "schema" -> "{}"
          )
        ))

        // testing response headers
        status(result) must equalTo(CREATED)
        contentType(result) must beSome("application/json")

        // testing response body
        contentAsString(result) must beEqualTo("""{"name":"MyNewType","schema":"{}"}""")
      }
    }

    "create a content type with description" in {
      running(FakeApplication()) {
        // delete all
        Neo4jUtils.deleteAll()

        val Some(result) = route(FakeRequest(POST, "/api/types").withBody(
          Json.obj(
            "name" -> "MyNewType",
            "schema" -> "{}",
            "description" -> "Just a description"
          )
        ))

        // testing response headers
        status(result) must equalTo(CREATED)
        contentType(result) must beSome("application/json")

        // testing response body
        contentAsString(result) must beEqualTo("""{"name":"MyNewType","description":"Just a description","schema":"{}"}""")
      }
    }

    "update a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(PUT, "/api/types/Tag").withBody(
          Json.obj(
            "name" -> "Tag",
            "schema" -> "{}",
            "description" -> "Just a Tag type"
          )
        ))

        // testing response headers
        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")

        // testing response body
        contentAsString(result) must beEqualTo("""{"name":"Tag","description":"Just a Tag type","schema":"{}"}""")
      }
    }

    "delete a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(DELETE, "/api/types/Taq"))

        status(result) must equalTo(NO_CONTENT)
      }
    }

  }

}
