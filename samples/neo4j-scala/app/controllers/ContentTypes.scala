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

  implicit val contentTypeReads = Json.reads[ContentType]
  implicit val contentTypeWrites = Json.writes[ContentType]

  def list = Action.async {
    ContentType.list().map(
      seqContentType =>
        Ok(seqContentType.toString)
    )
  }

  def get(name :String) = Action.async {
    ContentType.get(name).map(
      optionContentType => optionContentType match {
        case Some(contentType) =>
          Ok(contentType.toString)
        case _ =>
          NotFound
      }
    )
  }

  def create = Action {  implicit request =>
    Ok
  }

  def update(name :String) = Action { implicit request =>
    Ok
  }

  def delete(name :String) = Action.async {
    ContentType.delete(name).map(
      isOk => {
        if(isOk) Ok
        else InternalServerError
      }
    )
  }

}
