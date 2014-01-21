import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.Logger
import services.Neo4jService

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class Neo4jServiceSpec extends Specification {

  "Neo4jRESTService" should {

    "execute multiple cypher create query" in {
      running(FakeApplication()) {
        val api = new Neo4jService("http://localhost:7575")
        val queries = Array(("CREATE (n {props})", Map("name" -> "FRANCE", "pop" -> "100")), ("CREATE (n {props})", Map("name" -> "BELGIQUE", "pop" -> "10")))
        val result  = Helpers.await(api.cypher(queries))
        Logger.debug("Result is :" + result.right.toString)
        result.isRight must beTrue
      }
    }

    "execute single cypher create query" in {
      running(FakeApplication()) {
        val api = new Neo4jService("http://localhost:7575")
        val result  = Helpers.await(api.cypher("CREATE (n {props})", Map("name" -> "ALLEMAGNE", "pop" -> "100")))
        Logger.debug("Result is :" + result.right.toString)
        result.isRight must beTrue
      }
    }

    "execute cypher select query" in {
      running(FakeApplication()) {
        val api = new Neo4jService("http://localhost:7575")
        val result  = Helpers.await(api.cypher("MATCH (n) RETURN n LIMIT 100", Map()))
        Logger.debug("Result is :" + result.right.toString)
        result.isRight must beTrue
      }
    }
  }
}