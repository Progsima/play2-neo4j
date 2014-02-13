package controllers

import models.Content
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import com.wordnik.swagger.annotations._
import javax.ws.rs.PathParam
import scala.concurrent.Future

/**
 * REST API endpoint for Content.
 *
 * @author : bsimard
 */
@Api(value = "/contents", description = "Operations about content")
object Contents extends Controller {

  /**
   * List all content in the platform of the specified content type
   */
  @ApiOperation(
    value = "List content of the specified content type",
    notes = "Returns a list of content",
    httpMethod = "GET")
  def list(
            @ApiParam(value = "Name of the content type", required = true) @PathParam("contentType") contentType :String
  ) = Action.async { implicit request =>

    Content.list(contentType).map(
      seqContentType =>
        Ok(
          Json.toJson(seqContentType)
        ).as("application/json")
    )

  }

  /**
   * Get details of the specified content.
   */
  @ApiOperation(
    value = "Get a content by its type & uuid",
    notes = "Returns a content",
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Content not found")
  ))
  def get(
           @ApiParam(value = "Name of the content type", required = true) @PathParam("contentType") contentType :String,
           @ApiParam(value = "UUID of the content type", required = true) @PathParam("uuid") uuid :String
  ) = Action.async {

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
  @ApiOperation(
    value = "Create a new content type",
    notes = "Returns the content created",
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Content type that needs to be created", required = true, dataType = "Content", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "Content Type created"),
    new ApiResponse(code = 400, message = "Invalid body request"),
    new ApiResponse(code = 409, message = "Constraint violation")
  ))
  def create(
              @ApiParam(value = "Name of the content type", required = true) @PathParam("contentType") contentType :String
  ) = Action.async(parse.json) { implicit request =>

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
  @ApiOperation(
    value = "Update a content by its type & uuid",
    notes = "Returns the content updated",
    httpMethod = "PUT"
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Content that needs to be updates", required = true, dataType = "Content", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid body request"),
    new ApiResponse(code = 404, message = "Content Type not found"),
    new ApiResponse(code = 409, message = "Constraint violation")
  ))
  def update(
              @ApiParam(value = "Type of the content", required = true) @PathParam("contentType") contentType :String,
              @ApiParam(value = "UUID of the content", required = true) @PathParam("uuid") uuid: String
  ) = Action.async(parse.json) { implicit request =>

    Content.update(contentType, uuid, request.body).map {
      case Some(content) => Created(content).as("application/json")
      case _ => InternalServerError("Something went wrong when saving content...")
    }

  }

  /**
   * Delete the specified content type.
   */
  @ApiOperation(
    value = "Delete a content by its type & uuid",
    response = classOf[Void],
    httpMethod = "DELETE"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 204, message = "Success")
  ))
  def delete(
              @ApiParam(value = "Type of the content", required = true) @PathParam("contentType") contentType :String,
              @ApiParam(value = "UUID of the content", required = true) @PathParam("uuid") uuid: String
  ) = Action.async(parse.empty) { implicit request =>

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
