package com.logisima.play.neo4j.service

import com.logisima.play.neo4j.exception.Neo4jException
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.ws.Response
import play.Logger
import scala.Predef._
import scala.concurrent.Future
import scala._

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
    ("Content-Type", "application/json"),
    ("X-Stream" -> "true")
  )

  /**
   * Play JSON format for read/write Map[String Any] that represent cypher params.
   * @see anmorcypher source code : https://github.com/AnormCypher/AnormCypher/blob/master/src/main/scala/org/anormcypher/Neo4jREST.scala
   */
  implicit val mapFormat = new Format[Map[String, _]] {
    def read(xs: Seq[(String, JsValue)]): Map[String, _] = (xs map {
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

    def writes(map: Map[String, _]) =
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
  def doSingleCypherQuery(query: String, params: Map[String, _] = Map[String, Any](), transactionId: Option[Int] = None): Future[Seq[JsValue]] = {
    val result = this.doCypherQuery(Array((query, params)), transactionId)
    for (datas <- result) yield {
      if (datas.size > 0) {
        datas.apply(0)
      }
      else {
        Seq.apply()
      }
    }
  }

  /**
   * Execute a list of cypher query (with theirs params) into the specified transaction.
   * This method return a list of json that represent datas, or a neo4jExeption.
   *
   * @param queries
   * @param transactionId
   * @return
   */
  def doCypherQuery(queries: Array[(String, Map[String, _])], transactionId: Option[Int]): Future[Array[Seq[JsValue]]] = {
    // here we parse the response
    for (response <- constructAndSend(queries, transactionId)) yield {

      // Status is OK, let's look inside the JSON
      if (response.status == 200 || response.status == 201) {
        Logger.debug("[Transaction]: Status code is 200/201 " + Json.prettyPrint(response.json))

        // Checking errors
        parseErrors(response.json) match {
          case Some(exception: Neo4jException) => throw exception
          case _ => {
            val results = response.json.\("results").as[JsArray].value
            results.map {
              result :JsValue => {
                result.\("data").\\("row").map {
                  datas => datas.as[JsArray].apply(0)
                }
              }
            }.toArray
          }
        }
      }
      // Status is not 200 (or 201) : this shouldn't happen with transaction endpoint...
      else {
        throw new Neo4jException("Neo4j REST Transactional API error", "Transaction cypher status code is " + response.status)
      }
    }
  }

  /**
   * Helper that create the query and send it to neo4j.
   *
   * @param queries
   * @param transactionId : if None => /commit
   * @return
   */
  private def constructAndSend(queries: Array[(String, Map[String, _])], transactionId: Option[Int]): Future[Response] = {
    val url = transactionId match {
      case Some(id: Int) => rootUrl + "/db/data/transaction/" + id
      case _ => rootUrl + "/db/data/transaction/commit"
    }

    // let's construct the JSON body of the query
    val statements = queries.foldLeft(JsArray()) {
      (json, query) =>
        query match {
          case (cypher: String, params: Map[String, Any]) => {
            json.append(
              Json.obj(
                "statement" -> cypher,
                "parameters" -> Json.toJson(params)
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

  /**
   * Begin a transaction.
   *
   * @return A future response with the identifier of the transaction
   */
  def beginTx(): Future[Int] = {
    val url = rootUrl + "/db/data/transaction/"
    val transactionLocation = """(.*)/db/data/transaction/(\d+)""".r
    val result = WS.url(url)
      .withHeaders(stdHeaders: _*)
      .post("")

    for (response <- result) yield {
      parseErrors(response.json) match {
        case Some(e) => throw e
        case None => {
          response.header("Location") match {
            case Some(location: String) => {
              location match {
                case transactionLocation(url: String, transId: String) => transId.toInt
                case _ => throw new Neo4jException("Neo4j REST Transactional API error", "Location header is not parsable")
              }
            }
            case _ => throw new Neo4jException("Neo4j REST Transactional API error", "Location header is not present")
          }
        }
      }
    }
  }

  /**
   * Commit the specified transaction.
   *
   * @param transId
   * @return
   */
  def commit(transId: Int) :Future[Boolean] = {
    val url = rootUrl + "/db/data/transaction/" + transId + "/commit"
    val result = WS.url(url)
      .withHeaders(stdHeaders: _*)
      .post("")

    for (response <- result) yield {
      parseErrors(response.json) match {
        case Some(e: Neo4jException) => throw e
        case _ => true
      }
    }
  }

  /**
   * Rollback the specified transaction.
   *
   * @param transId
   */
  def rollBack(transId: Int) :Future[Boolean] =  {
    val url = rootUrl + "/db/data/transaction/" + transId
    val result = WS.url(url)
      .withHeaders(stdHeaders: _*)
      .delete()

    for (response <- result) yield {
      parseErrors(response.json) match {
        case Some(e: Neo4jException) => throw e
        case _ => true
      }
    }
  }

  /**
   * Parse errors rom the Neo4j response.
   *
   * @param response
   * @return
   */
  private def parseErrors(response: JsValue): Option[Neo4jException] = {
    var errors: Seq[String] = Seq.apply()
    if (response.\("errors").toString != "[]") {
      errors = response.\\("errors").map {
        error =>
          Logger.debug("" + error(0))
          val code: Option[String] = (error(0) \ ("code")).asOpt[String]
          val message: Option[String] = (error(0) \ ("message")).asOpt[String]
          Logger.debug("[Transaction]: Neo4jError is " + code + " " + message)
          code.getOrElse("") + " : " + message.getOrElse("")
      }
      Option.apply(
        new Neo4jException(
          "Neo4j REST Transactional API error",
          errors.foldLeft("") {
            (message, exception) => message + exception + "\n\n"
          }
        )
      )
    }
    else {
      Option.empty
    }
  }
}
