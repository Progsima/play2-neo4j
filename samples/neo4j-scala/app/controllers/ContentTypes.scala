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
@Api(value = "/types", description = "Operations about content type")
object ContentTypes extends Controller {

  /**
   * List all content type in the plateform.
   */
  @ApiOperation(
    value = "List content type",
    notes = "Returns a list of content type",
    httpMethod = "GET"
  )
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
   */
  @ApiOperation(
    value = "Get a content type by its name",
    notes = "Returns a content type",
    response = classOf[models.ContentType],
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Content Type not found")
  ))
  def get(
           @ApiParam(value = "Name of the content type", required = true) @PathParam("name") name: String
  ) = Action.async {

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
  @ApiOperation(
    value = "Create a new content type",
    notes = "Returns the content type created",
    response = classOf[models.ContentType],
    httpMethod = "POST"
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Content type that needs to be created", required = true, dataType = "ContentType", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Content Type created"),
    new ApiResponse(code = 400, message = "Invalid body request"),
    new ApiResponse(code = 409, message = "Constraint violation")
  ))
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
   */
  @ApiOperation(
    value = "Update a content type by its name",
    notes = "Returns the content type updated",
    response = classOf[models.ContentType],
    httpMethod = "PUT"
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Content type that needs to be updates", required = true, dataType = "ContentType", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid body request"),
    new ApiResponse(code = 404, message = "Content Type not found"),
    new ApiResponse(code = 409, message = "Constraint violation")
  ))
  def update(@ApiParam(value = "Name of the content type", required = true) @PathParam("name") name: String) = Action.async(parse.json) { implicit request =>

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
  @ApiOperation(
    value = "Delete a content type by its name",
    response = classOf[Void],
    httpMethod = "DELETE")
  @ApiResponses(Array(new ApiResponse(code = 204, message = "Success")))
  def delete(@ApiParam(value = "Name of the content type", required = true) @PathParam("name") name: String) = Action.async(parse.empty) {
    implicit request =>

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
