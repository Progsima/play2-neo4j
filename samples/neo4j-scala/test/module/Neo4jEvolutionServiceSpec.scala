package module

import com.logisima.play.neo4j.Neo4j
import com.logisima.play.neo4j.service.Neo4jEvolutionService
import com.logisima.play.neo4j.utils._
import java.io.File
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.Play
import scala.io.Source
import com.logisima.play.neo4j.item.{EvolutionFeatureMode, Evolution}

/**
 * Spec to test Neo4j evolution service.
 */
class Neo4jEvolutionServiceSpec extends Specification {

  "Neo4jEvolutionService" should {

    "get cypher up script" in {
      running(FakeApplication()) {
        val evolutions :Seq[Evolution] = Neo4jEvolutionService.applicationEvolutions()

        evolutions.size must beEqualTo(4)
      }
    }

    "transform evolution script to statements" in {
      running(FakeApplication()) {
        val script :String = FileUtils.getFile(Play.application().path().getPath + "/conf/evolutions/neo4j/1_up.cql") match {
          case Some(file: File) => Source.fromFile(file).getLines() mkString "\n"
          case None => ""
        }

        val evolutions = Neo4jEvolutionService.statements(script)

        evolutions.size must beEqualTo(2)
        evolutions(0) must beEqualTo("create (germany:Country {name:\"Germany\", population:81726000, type:\"Country\", code:\"DEU\"}),\n       (france:Country {name:\"France\", population:65436552, type:\"Country\", code:\"FRA\", indepYear:1790})")
      }
    }

    "execute update" in {
      running(FakeApplication()) {
        // delete the entire database
        Neo4jUtils.reset()

        Neo4jEvolutionService.checkEvolutionState(EvolutionFeatureMode.auto)

        Helpers.await(Neo4j.cypher("MATCH (n:Country) RETURN n LIMIT 100")).size must beGreaterThan(0)
      }
    }
  }
}