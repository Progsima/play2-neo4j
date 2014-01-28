import com.logisima.play.neo4j.evolution.{EvolutionFeatureMode, CypherScriptType, Evolution}
import com.logisima.play.neo4j.exception.Neo4jException
import com.logisima.play.neo4j.Neo4j
import com.logisima.play.neo4j.service.{Neo4jTransactionalService, Neo4jEvolutionService}
import com.logisima.play.neo4j.utils.{Neo4jUtils, FileUtils}
import java.io.File
import org.specs2.mutable._

import play.api.libs.json.JsValue
import play.api.test._
import play.api.test.Helpers._
import play.{Logger, Play}
import scala.io.Source

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class Neo4jEvolutionServiceSpec extends Specification {

  "Neo4jEvolutionService" should {

    "get cypher up script" in {
      running(FakeApplication()) {
        val evolution :Neo4jEvolutionService = new Neo4jEvolutionService(Neo4j.serverUrl)
        val evolutions :Seq[Evolution] = evolution.applicationEvolutions()

        evolutions.size must beEqualTo(2)
      }
    }

    "transform evolution script to statements" in {
      running(FakeApplication()) {
        val evolution :Neo4jEvolutionService = new Neo4jEvolutionService(Neo4j.serverUrl)
        val script :String = FileUtils.getFile(Play.application().path().getPath + "/conf/evolutions/neo4j/1_up.cql") match {
          case Some(file: File) => Source.fromFile(file).getLines() mkString "\n"
          case None => ""
        }

        val evolutions = evolution.statements(script)

        evolutions.size must beEqualTo(2)
        evolutions(0) must beEqualTo("create (germany:Country {name:\"Germany\", population:81726000, type:\"Country\", code:\"DEU\"}),\n       (france:Country {name:\"France\", population:65436552, type:\"Country\", code:\"FRA\", indepYear:1790})")
      }
    }

    "execute update" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()

        val evolution: Neo4jEvolutionService = new Neo4jEvolutionService(Neo4j.serverUrl)
        evolution.checkEvolutionState(EvolutionFeatureMode.auto)

        val api = new Neo4jTransactionalService(Neo4j.serverUrl)
        val result: Either[Neo4jException, Seq[JsValue]] = Helpers.await(api.cypher("MATCH (n:Country) RETURN n LIMIT 100"))
        val rsSize: Int = result match {
          case Left(x) => 0
          case Right(x) => x.map(
            jsValue => {
              jsValue(0)
            })
            x.size
        }
        rsSize must beGreaterThan(0)
      }
    }
  }
}