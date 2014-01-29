import com.logisima.play.neo4j.evolution.EvolutionFeatureMode
import com.logisima.play.neo4j.exception.Neo4jException
import com.logisima.play.neo4j.Neo4j
import com.logisima.play.neo4j.service.{Neo4jEvolutionService, Neo4jTransactionalService}
import com.logisima.play.neo4j.utils.Neo4jUtils
import org.specs2.mutable._

import play.api.libs.json._
import play.api.libs.functional.syntax._
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

        val api = new Neo4jTransactionalService(Neo4j.serverUrl)
        val queries = Array(("CREATE (n:Pays {props})", Map("name" -> "FRANCE", "pop" -> 100)), ("CREATE (n:Pays {props})", Map("name" -> "BELGIQUE", "pop" -> 10)))
        val result = Helpers.await(api.cypher(queries))
        Logger.debug("Result is :" + result.right.toString)
        result.isRight must beTrue
      }
    }

    "execute single cypher create query" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()

        val api = new Neo4jTransactionalService(Neo4j.serverUrl)
        val result: Either[Neo4jException, Seq[JsValue]] = Helpers.await(api.cypher("CREATE (n:Pays {props})", Map("name" -> "ALLEMAGNE", "pop" -> 100)))
        Logger.debug("Result is :" + result.right.toString)
        result.isRight must beTrue
      }
    }

    "execute cypher select query without params" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()
        // populate the database by runnig evolution script
        new Neo4jEvolutionService(Neo4j.serverUrl).checkEvolutionState(EvolutionFeatureMode.auto)

        val api = new Neo4jTransactionalService(Neo4j.serverUrl)
        val result: Either[Neo4jException, Seq[JsValue]] = Helpers.await(api.cypher("MATCH (n:Country) RETURN n LIMIT 100"))


        case class Country(name: String, pop: Int)
        implicit val countryReads = Json.reads[Country]

        val rsSize: Int = result match {
          case Left(x) => 0
          case Right(x) => {
            x.map(
              jsValue => {
                Logger.debug("JsValue is " + jsValue.apply(0))
                Logger.debug("Object is " + Json.fromJson[Country](jsValue.apply(0)))
                Json.fromJson[Country](jsValue.apply(0))
              }
            )
            Logger.debug("Value is " + x)
            x.size
          }
        }
        rsSize must beEqualTo(6)
      }
    }

  }
}