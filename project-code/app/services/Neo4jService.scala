package services

/**
 * Class that handle neo4j exception into poorly format.
 *
 * @param message
 */
class Neo4jException(message :String){}

import play.api.libs.ws.WS
import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.Logger
import scala.Predef._
import play.api.libs.ws.Response
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Neo4j helper for its REST API.
 */
class Neo4jService(rootUrl: String) {

  /**
   * Default headers for all API call.
   */
  val stdHeaders = Seq(
    ("Accept", "application/json"),
    ("Content-Type", "application/json")
  )


  def cypher(queries :Array[(String,Map[String, String])]) :Future[Either[Neo4jException,Array[Seq[JsValue]]]] = {
    val url = rootUrl + "/db/data/transaction/commit"
    val statements = queries.foldLeft(JsArray()) { (json, query) =>
      query match {
        case (cypher :String, params :Map[String, String]) => {
          json.append(
            Json.obj(
              "statement" -> cypher,
              "parameters" -> Json.obj(
                "props" -> JsObject(params.map {case (key :String, value :Any) => (key, Json.toJson(value))}.toSeq)
              )
            )
          )
        }
      }
    }
    val body = Json.obj("statements" -> statements)
    Logger.debug("Calling API endpoint " + url + " with body " + body)

    val result :Future[Response] = WS.url(url)
      .withHeaders(stdHeaders: _*)
      .post(
        body
      )
    for(response <- result) yield {
      if( response.status == 200 ) {
        Logger.debug("Status code is 200 :" + response.body)
        val datas = response.json.\\("results").map { data =>
          data.\\("data")
        }
        Right(datas.toArray)
      }
      else {
        Logger.debug("Status code is not 200 :" + response.body)
         Left(
           new Neo4jException(
             response.json.\\("errors").toString()
           )
         )
      }
    }
  }

}
