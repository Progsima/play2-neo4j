package controllers

import models.ContentType
import models.ContentType.contentTypeWrites
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._

/**
 * Created by bsimard on 03/02/14.
 */
object ContentTypes extends Controller {

  /**
   * List all content type in the plateform.
   *
   * @return
   */
  def list = Action.async {
    ContentType.list().map(
      seqContentType =>
        Ok(Json.toJson(seqContentType)).as("application/json")
    )
  }

  /**
   * Get details of the specified content type (ny its name).
   *
   * @param name
   * @return
   */
  def get(name :String) = Action.async {
    ContentType.get(name).map(
      optionContentType => optionContentType match {
        case Some(contentType) =>
          Ok(Json.toJson(contentType)).as("application/json")
        case _ =>
          NotFound
      }
    )
  }

  /**
   * Create a content type.
   *
   * @return
   */
  def create = Action {  implicit request =>
    Ok
  }

  /**
   * Update the specified content type.
   *
   * @param name
   * @return
   */
  def update(name :String) = Action { implicit request =>
    Ok
  }

  /**
   * Delete the specified content type.
   *
   * @param name
   * @return
   */
  def delete(name :String) = Action.async {
    ContentType.delete(name).map(
      isOk => {
        if(isOk) Ok
        else InternalServerError
      }
    )
  }

}
