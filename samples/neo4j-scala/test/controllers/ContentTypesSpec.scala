package controllers

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}

/**
 * Created by bsimard on 09/02/14.
 */
class ContentTypesSpec extends Specification {

  "API Content type" should {

    "list content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/types"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        charset(result) must beSome("utf-8")
      }
    }

    "get a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/types/Tag"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        charset(result) must beSome("utf-8")
      }
    }

    "create a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(PUT, "/types"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        charset(result) must beSome("utf-8")
      }
    }

    "update a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(POST, "/types/Tag"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        charset(result) must beSome("utf-8")
      }
    }

    "delete a content type" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(DELETE, "/types/Tag"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        charset(result) must beSome("utf-8")
      }
    }

  }

}
