package controllers

import models.ContentType
import models.ContentType.contentTypeWrites
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import com.wordnik.swagger.annotations._
import javax.ws.rs.PathParam

/**
 * REST API endpoint for Content type.
 *
 * @author ; bsimard
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
  def list = Action.async {
    implicit request =>

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
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Content Type not found")
  ))
  def get(
           @ApiParam(value = "Name of the content type", required = true) @PathParam("name") name: String
           ) = Action.async {

    ContentType.get(name).map(
      optionContentType => optionContentType match {
        case Some(contentType) =>
          Ok(
            Json.toJson(contentType)
          ).as("application/json")
        case _ =>
          NotFound("Content Type not found")
      }
    )
  }

  /**
   * Create a content type.
   * This a POST because it's not safe & idempotent (there is a unique constraint on name).
   * If succeed, we return a 201 with object into the body
   *
   * @return
   */
  @ApiOperation(
    value = "Create a new content type",
    notes = "Returns the content type created",
    response = classOf[models.ContentType],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Content type that needs to be created", required = true, dataType = "ContentType", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Content Type created"),
    new ApiResponse(code = 400, message = "Invalid body request"),
    new ApiResponse(code = 404, message = "Content Type not found"),
    new ApiResponse(code = 409, message = "Constraint violation")
  ))
  def create() = Action.async(parse.json) {
    implicit request =>

      ContentType.create(request.body).map(
        optionContentType => optionContentType match {
          case Some(contentType) => {
            Created(Json.toJson(contentType)).as("application/json")
          }
          case _ => NotFound
        }
      )

  }

  /**
   * Update the specified content type.
   *
   * @param name
   * @return
   */
  @ApiOperation(
    value = "Update a content type by its name",
    notes = "Returns the content type updated",
    response = classOf[models.ContentType],
    httpMethod = "PUT")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Content type that needs to be updates", required = true, dataType = "ContentType", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid body request"),
    new ApiResponse(code = 404, message = "Content Type not found"),
    new ApiResponse(code = 409, message = "Constraint violation")
  ))
  def update(
              @ApiParam(value = "Name of the content type", required = true) @PathParam("name") name: String
              ) = Action.async(parse.json) {
    implicit request =>

    // retrieve the specified content type
      ContentType.update(name, request.body).map(
        optionContentType => optionContentType match {
          case Some(contentType) => {
            Ok(Json.toJson(contentType)).as("application/json")
          }
          case _ => NotFound("Content Type not found")
        }
      )

  }

  /**
   * Delete the specified content type.
   *
   * @param name
   * @return
   */
  @ApiOperation(
    value = "Delete a content type by its name",
    response = classOf[Void],
    httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 204, message = "No content")
  ))
  def delete(
              @ApiParam(value = "Name of the content type", required = true) @PathParam("name") name: String
              ) = Action.async(parse.empty) {
    implicit request =>

      for (isOk <- ContentType.delete(name)) yield {
        if (isOk == true) {
          NoContent
        }
        else {
          InternalServerError
        }
      }

  }

}
