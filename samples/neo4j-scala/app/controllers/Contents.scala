package controllers

import models.Content
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._

/**
 * REST API endpoint for Content.
 *
 * @author : bsimard
 */
object Contents extends Controller {

  /**
   * List all content in the platform of the specified content type
   */
  def list( contentType :String, page :Int, row :Int, sort :String, order :String, filter :String ) = Action.async { implicit request =>

    for ( (total, data) <- Content.list(contentType, page, row, sort, order, filter)) yield {
      Ok(Json.toJson(data))
        .as("application/json")
        .withHeaders(("X-Total-Row" -> total.toString))
    }

  }

  /**
   * Get details of the specified content.
   */
  def get( contentType :String, uuid :String) = Action.async {

    Content.get(contentType, uuid).map {
      case Some(content) => Ok(content).as("application/json")
      case None => NotFound
    }

  }

  /**
   * Create a content.
   * This a POST because it's not safe & idempotent (there is a unique constraint on name).
   * If succeed, we return a 201 with object into the body
   */
  def create( contentType :String ) = Action.async(parse.json) { implicit request =>

    Content.create(contentType, request.body).map {
        case Some(content) => Created(content).as("application/json")
        case _ => InternalServerError("Something went wrong when saving content...")
    }
  }


  /**
   * Update the specified content.
   * This a PUT because it's pretty much idempotent except if you change the uuid of the object (that should not be allowed).
   * If succeed, we return a 200 & the updated content.
   */
  def update( contentType :String, uuid: String ) = Action.async(parse.json) { implicit request =>

    Content.update(contentType, uuid, request.body).map {
      case Some(content) => Created(content).as("application/json")
      case _ => InternalServerError("Something went wrong when saving content...")
    }

  }

  /**
   * Delete the specified content type.
   */
  def delete( contentType :String, uuid: String ) = Action.async(parse.empty) { implicit request =>

    for (isOk <- Content.delete(contentType, uuid)) yield {
      if (isOk) {
        NoContent
      }
      else {
        InternalServerError
      }
    }

  }

}
