package controllers

import models.ContentType
import models.ContentType.contentTypeWrites
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import com.wordnik.swagger.annotations._
import javax.ws.rs.PathParam
import tools.JsonTools
import scala.concurrent.Future

/**
 * REST API endpoint for Content type.
 *
 * @author : bsimard
 */
object ContentTypes extends Controller {

  /**
   * List all content type in the plateform.
   */
  def list(page :Int, row :Int, sort :String, order :String, filter :String) = Action.async { implicit request =>

    for ( (total, data) <- ContentType.list(page, row, sort, order, filter)) yield {
      Ok(Json.toJson(data))
        .as("application/json")
        .withHeaders(("X-Total-Row" -> total.toString))
    }

  }

  /**
   * Get details of the specified content type.
   */
  def get(name: String ) = Action.async {

    ContentType.get(name).map {
      case Some(contentType) => Ok(Json.toJson(contentType)).as("application/json")
      case None => NotFound
    }

  }

  /**
   * Create a content type.
   * This a POST because it's not safe & idempotent (there is a unique constraint on name).
   * If succeed, we return a 201 with object into the body
   */
  def create() = Action.async(parse.json) { implicit request =>

      JsonTools.validateSchema((request.body \ "schema").as[String]) match {

        case Some(errors) => Future {
          BadRequest(Json.toJson(errors)).as("application/json")
        }

        case None => ContentType.create(request.body).map {
          case Some(contentType) => Created(Json.toJson(contentType)).as("application/json")
          case _ => InternalServerError("Something went wrong when saving content type...")
        }
      }

  }

  /**
   * Update the specified content type.
   * It's a PUT.
   */
  def update( name: String ) = Action.async(parse.json) { implicit request =>

      JsonTools.validateSchema((request.body \ "schema").as[String]) match {
        case Some(errors) => Future { BadRequest(Json.toJson(errors)) }
        case None =>
          // retrieve the specified content type
          ContentType.update(name, request.body).map {
            case Some(contentType) => Ok(Json.toJson(contentType)).as("application/json")
            case _ => NotFound("Content Type not found")
          }
      }

  }

  /**
   * Delete the specified content type.
   */
  def delete( name: String ) = Action.async(parse.empty) { implicit request =>

      for (isOk <- ContentType.delete(name)) yield {
        if (isOk) {
          NoContent
        }
        else {
          InternalServerError
        }
      }

  }

}
