package module

import com.logisima.play.neo4j.evolution.EvolutionFeatureMode
import com.logisima.play.neo4j.exception._
import com.logisima.play.neo4j.Neo4j
import com.logisima.play.neo4j.service.{Neo4jEvolutionService, Neo4jTransactionalService}
import com.logisima.play.neo4j.utils.Neo4jUtils
import org.specs2.mutable._

import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._

import play.Logger

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class Neo4jTransactionServiceSpec extends Specification {

  "Neo4jTransactionService" should {

    "execute multiple cypher create query" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()

        val queries = Array(
          ("CREATE (n:Pays {name: {name}, pop:{pop}})", Map("name" -> "FRANCE", "pop" -> 100)),
          ("CREATE (n:Pays {name: {name}, pop:{pop}})", Map("name" -> "BELGIQUE", "pop" -> 10)),
          ("CREATE (n:test {name: {name}})", Map("name" -> "toto"))
        )
        val result = Helpers.await(Neo4j.cypher(queries))
        Logger.debug("Result is :" + result)
        result.size must beEqualTo(3)
      }
    }

    "execute multiple cypher select query" in {
      running(FakeApplication()) {

        val queries = Array(
          ("MATCH (n:Country) RETURN n LIMIT 100", Map[String, Any]()),
          ("MATCH (n:Country) RETURN n LIMIT 100", Map[String, Any]()),
          ("MATCH (n:Country) RETURN n LIMIT 100", Map[String, Any]())
        )
        val result = Helpers.await(Neo4j.cypher(queries))
        Logger.debug("Result is :" + result)
        result.size must beEqualTo(3)
        result(0).size must beEqualTo(6)
        result(1).size must beEqualTo(6)
        result(2).size must beEqualTo(6)
      }
    }

    "execute single cypher create query" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()

        val result = Helpers.await(Neo4j.cypher("CREATE (n:Pays {props})", Map("props" -> Map("name" -> "ALLEMAGNE", "pop" -> 100))))
        Logger.debug("Result is :" + result.toString)
        result.size must beEqualTo(0)
      }
    }

    "execute cypher select query without params" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()
        // populate the database by running evolution script
        Neo4jEvolutionService.checkEvolutionState(EvolutionFeatureMode.auto)

        Helpers.await(Neo4j.cypher("MATCH (n:Country) RETURN n LIMIT 100")).size must beEqualTo(6)
      }
    }

    "return error on bad cypher query" in {
      running(FakeApplication()) {

        Helpers.await(Neo4j.cypher("MATCH (n) RETURN M LIMIT 100")) must throwA[Neo4jException]
      }
    }

    "can start a transaction" in {
      running(FakeApplication()) {
        val api = new Neo4jTransactionalService(Neo4j.serverUrl)
        val transId = Helpers.await(api.beginTx())
        transId must beGreaterThanOrEqualTo(0)
      }
    }

    "can start a transaction & commit" in {
      running(FakeApplication()) {
        val transId = Helpers.await(Neo4j.beginTx())

        Helpers.await(Neo4j.cypher("CREATE (n:DB {props})", Map("props" -> Map("name" -> "Neo4j", "Type" -> "Graph")), transId))
        Helpers.await(Neo4j.cypher("CREATE (n:DB {props})", Map("props" -> Map("name" -> "CouchDb", "Type" -> "Document")),transId))
        Helpers.await(Neo4j.cypher("CREATE (n:DB {props})", Map("props" -> Map("name" -> "Postgres", "Type" -> "Relational")), transId))
        Helpers.await(Neo4j.commit(transId))

        val result: Seq[JsValue] = Helpers.await(Neo4j.cypher("MATCH (n:DB) RETURN n"))
        result.size must beEqualTo(3)
      }
    }

    "can start a transaction & rollback" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()

        val transId = Helpers.await(Neo4j.beginTx())
        Helpers.await(Neo4j.cypher("CREATE (n:DB {props})", Map("props" -> Map("name" -> "Neo4j", "Type" -> "Graph")), transId))
        Helpers.await(Neo4j.cypher("CREATE (n:DB {props})", Map("props" -> Map("name" -> "CouchDb", "Type" -> "Document")),transId))
        Helpers.await(Neo4j.cypher("CREATE (n:DB {props})", Map("props" -> Map("name" -> "Postgres", "Type" -> "Relational")), transId))
        Helpers.await(Neo4j.rollback(transId))

        val result = Helpers.await(Neo4j.cypher("MATCH (n:DB) RETURN n"))
        result.size must beEqualTo(0)
      }
    }

  }
}