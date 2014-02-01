package com.logisima.play.neo4j.service

import play.api.libs.ws.WS
import play.api.libs.json.Json._
import play.api.libs.json._
import play.Logger
import scala.Predef._
import scala.concurrent.Future
import scala._
import play.api.libs.json.JsArray
import play.api.libs.ws.Response
import play.api.libs.json.JsObject
import com.logisima.play.neo4j.exception.{Neo4jError, Neo4jException}
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Neo4j service that handle transaction REST API endpoint.
 *
 * @author : bsimard
 */
class Neo4jTransactionalService(rootUrl: String) {

  /**
   * Default headers for all API call.
   */
  val stdHeaders = Seq(
    ("Accept", "application/json"),
    ("Content-Type", "application/json")
  )

  /**
   * Play JSON format for read/write Map[String Any] that represent cypher params.
   * @see anmorcypher source code : https://github.com/AnormCypher/AnormCypher/blob/master/src/main/scala/org/anormcypher/Neo4jREST.scala
   */
  implicit val mapFormat = new Format[Map[String, Any]] {
    def read(xs: Seq[(String, JsValue)]): Map[String, Any] = (xs map {
      case (k, JsBoolean(b)) => k -> b
      case (k, JsNumber(n)) => k -> n
      case (k, JsString(s)) => k -> s
      case (k, JsArray(bs)) if (bs.forall(_.isInstanceOf[JsBoolean])) =>
        k -> bs.asInstanceOf[Seq[JsBoolean]].map(_.value)
      case (k, JsArray(ns)) if (ns.forall(_.isInstanceOf[JsNumber])) =>
        k -> ns.asInstanceOf[Seq[JsNumber]].map(_.value)
      case (k, JsArray(ss)) if (ss.forall(_.isInstanceOf[JsString])) =>
        k -> ss.asInstanceOf[Seq[JsString]].map(_.value)
      case (k, JsObject(o)) => k -> read(o)
      case _ => throw new RuntimeException(s"unsupported type")
    }).toMap

    def reads(json: JsValue) = json match {
      case JsObject(xs) => JsSuccess(read(xs))
      case x => JsError(s"json not of type Map[String, Any]: $x")
    }

    def writes(map: Map[String, Any]) =
      Json.obj(map.map {
        case (key, value) => {
          val ret: (String, JsValueWrapper) = value match {
            case b: Boolean => key -> JsBoolean(b)
            case b: Byte => key -> JsNumber(b)
            case s: Short => key -> JsNumber(s)
            case i: Int => key -> JsNumber(i)
            case l: Long => key -> JsNumber(l)
            case f: Float => key -> JsNumber(f)
            case d: Double => key -> JsNumber(d)
            case c: Char => key -> JsNumber(c)
            case s: String => key -> JsString(s)
            case bs: Seq[_] if (bs.forall(_.isInstanceOf[Boolean])) =>
              key -> JsArray(bs.map(b => JsBoolean(b.asInstanceOf[Boolean])))
            case bs: Seq[_] if (bs.forall(_.isInstanceOf[Byte])) =>
              key -> JsArray(bs.map(b => JsNumber(b.asInstanceOf[Byte])))
            case ss: Seq[_] if (ss.forall(_.isInstanceOf[Short])) =>
              key -> JsArray(ss.map(s => JsNumber(s.asInstanceOf[Short])))
            case is: Seq[_] if (is.forall(_.isInstanceOf[Int])) =>
              key -> JsArray(is.map(i => JsNumber(i.asInstanceOf[Int])))
            case ls: Seq[_] if (ls.forall(_.isInstanceOf[Long])) =>
              key -> JsArray(ls.map(l => JsNumber(l.asInstanceOf[Long])))
            case fs: Seq[_] if (fs.forall(_.isInstanceOf[Float])) =>
              key -> JsArray(fs.map(f => JsNumber(f.asInstanceOf[Float])))
            case ds: Seq[_] if (ds.forall(_.isInstanceOf[Double])) =>
              key -> JsArray(ds.map(d => JsNumber(d.asInstanceOf[Double])))
            case cs: Seq[_] if (cs.forall(_.isInstanceOf[Char])) =>
              key -> JsArray(cs.map(c => JsNumber(c.asInstanceOf[Char])))
            case ss: Seq[_] if (ss.forall(_.isInstanceOf[String])) =>
              key -> JsArray(ss.map(s => JsString(s.asInstanceOf[String])))
            case sam: Map[_, _] if (sam.keys.forall(_.isInstanceOf[String])) =>
              key -> writes(sam.asInstanceOf[Map[String, Any]])
            case xs: Seq[_] => throw new RuntimeException(s"unsupported Neo4j array type: $xs (mixed types?)")
            case x => throw new RuntimeException(s"unsupported Neo4j type: $x")
          }
          ret
        }
      }.toSeq: _*)
  }

  /**
   * Execute a unique cypher with its params (It's better to user params for perfomance).
   * This method return a list of json that represent datas, or a neo4jExeption.
   *
   * @param query
   * @param params
   * @return
   */
  def cypher(query: String, params: Map[String, Any] = Map[String, Any]()): Future[Either[Neo4jException, Seq[JsValue]]] = {
    val result = this.cypher(Array((query, params)))
    for (response <- result) yield {
      response match {
        case Left(exception: Neo4jException) => Left(exception)
        case Right(datas: Array[Seq[JsValue]]) => {
          if (datas.size > 0) {
            Right(datas.apply(0))
          }
          else {
            Right(Seq.apply())
          }
        }
      }
    }
  }

  /**
   * Execute a list of cypher query (with theirs params) in a the same neo4j transaction.
   * This method return a list of json that represent datas, or a neo4jExeption.
   *
   * @param queries
   * @return
   */
  def cypher(queries: Array[(String, Map[String, Any])]): Future[Either[Neo4jException, Array[Seq[JsValue]]]] = {
    // here we parse the response
    for (response <- constructAndSend(queries)) yield {

      // Status is OK, let's look inside the JSON
      if (response.status == 200 || response.status == 201) {
        Logger.debug("[Transaction]: Status code is 200/201 :")

        // first checking errors !
        Logger.debug("[Transaction]: JSON errors size is " + response.json.\\("errors").size + "[" + response.json.\\("errors") + "]")
        var errors: Seq[Neo4jError] = Seq.apply()
        if (response.json.\("errors").toString != "[]") {
          errors = response.json.\\("errors").map {
            error =>
              Logger.debug("" + error(0))
              val code: Option[String] = (error(0) \ ("code")).asOpt[String]
              val message: Option[String] = (error(0) \ ("message")).asOpt[String]
              Logger.debug("[Transaction]: Neo4jError is " + code + " " + message)
              new Neo4jError(code.getOrElse(""), message.getOrElse(""))
          }
        }
        Logger.debug("[Transaction]: Errors size is " + errors.size)
        if (errors.size == 0) {
          Logger.debug("[Transaction]: error is " + errors.size)
          val datas = response.json.\\("results").map {
            data =>
              data.\\("data").map {
                row => {
                  Logger.debug("[Transaction]: Row is " + row \\ ("row").toString)
                  row \\ ("row")
                }
              }
          }.flatten
          Right(datas.toArray)
        }
        else {
          Left(new Neo4jException(errors))
        }
      }
      // Status is not 200 (or 201) : this shouldn't happen with transaction endpoint...
      else {
        Logger.debug("[Transaction]: Status code is " + response.status + ":" + response.body)
        Left(
          new Neo4jException(Seq.apply(new Neo4jError("http.client", "Status code is " + response.status))
          )
        )
      }
    }
  }

  /**
   * Helper that create the query and send it to neo4j.
   *
   * @param queries
   * @return
   */
  private def constructAndSend(queries: Array[(String, Map[String, Any])]): Future[Response] = {
    val url = rootUrl + "/db/data/transaction/commit"
    // let's construct the JSON body of the query
    val statements = queries.foldLeft(JsArray()) {
      (json, query) =>
        query match {
          case (cypher: String, params: Map[String, Any]) => {
            json.append(
              Json.obj(
                "statement" -> cypher,
                "parameters" -> Json.obj(
                  "props" -> Json.toJson(params)
                )
              )
            )
          }
        }
    }
    val body = Json.obj("statements" -> statements)
    Logger.debug("[Transaction]: Calling API endpoint " + url + " with body " + body)

    WS.url(url)
      .withHeaders(stdHeaders: _*)
      .post(
        body
      )
  }

}
