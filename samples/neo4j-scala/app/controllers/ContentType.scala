package controllers

import play.api.mvc._

/**
 * Created by bsimard on 03/02/14.
 */
object ContentType extends Controller {

  def list = Action { implicit request =>
    Ok
  }

  def get(name :String) = Action {  implicit request =>
    Ok
  }

  def create = Action {  implicit request =>
    Ok
  }

  def update = Action { implicit request =>
    Ok
  }

  def delete(name :String) = Action { implicit request =>
    Ok
  }

  def search = Action { implicit request =>
    Ok
  }
}
