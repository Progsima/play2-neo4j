package controllers

import models.ContentType
import models.ContentType.contentTypeWrites
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import scala.concurrent.Future
import com.wordnik.swagger.annotations._
import javax.ws.rs.PathParam

/**
 * Created by bsimard on 03/02/14.
 */
@Api(value = "/types", description = "Operations about content type")
object ContentTypes extends Controller {

  /**
   * List all content type in the plateform.
   *
   * @return
   */
  @ApiOperation(
    value = "List content type",
    notes = "Returns a list of content type",
    httpMethod = "GET")
  def list = Action.async { implicit request =>
    ContentType.list().map(
      seqContentType =>
        Ok(
          Json.toJson(seqContentType)
        ).as("application/json")
    )
  }

  /**
   * Get details of the specified content type.
   *
   * @param name
   * @return
   */
  @ApiOperation(
    value = "Get a content type by its name",
    notes = "Returns a content type",
    response = classOf[models.ContentType],
    httpMethod = "GET")
  def get(
           @ApiParam(value = "ID of the pet to fetch") @PathParam("name") name :String
           ) = Action.async {

    ContentType.get(name).map(
      optionContentType => optionContentType match {
        case Some(contentType) =>
          Ok(
            Json.toJson(contentType)
          ).as("application/json")
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
  //def create(json :String) = Action.async {
  //  new Future[Ok()]
  //}

  /**
   * Update the specified content type.
   *
   * @param name
   * @return
   */
  def update(name :String, json :String) = Action.async {
    // retrieve the specified content type
    ContentType.get(name).map(
      optionContentType => optionContentType match {
        case Some(contentType) => {
            Ok
        }
        case _ => NotFound
      }
    )
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
