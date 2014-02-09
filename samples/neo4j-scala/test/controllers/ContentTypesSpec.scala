package controllers

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import play.api.libs.json.{Json, JsObject}

/**
 * Spec to test Content Type controller of the sample application.
 */
class ContentTypesSpec extends Specification {

  "API Content type" should {

    "list content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/api/types"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
      }
    }

    "get a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/api/types/Tag"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
      }
    }

    "create a content type without description" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(POST, "/api/types").withBody(
          Json.obj(
            "name" -> "test1",
            "schema" -> "{}"
          )
        ))
        status(result) must equalTo(CREATED)
        contentType(result) must beSome("application/json")
      }
    }

    "create a content type with description" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(POST, "/api/types").withBody(
          Json.obj(
            "name" -> "test2",
            "schema" -> "{}",
            "description" -> "Just a description"
          )
        ))
        status(result) must equalTo(CREATED)
        contentType(result) must beSome("application/json")
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

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
      }
    }

    "delete a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(DELETE, "/api/types/Faq"))

        status(result) must equalTo(NO_CONTENT)
      }
    }

  }

}
