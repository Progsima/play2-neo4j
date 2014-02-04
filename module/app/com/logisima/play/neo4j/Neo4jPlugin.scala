package com.logisima.play.neo4j

import play.api.Play.current
import play.api.{Play, Logger, Plugin}
import play.core.HandleWebCommandSupport
import com.logisima.play.neo4j.service.Neo4jEvolutionService
import com.logisima.play.neo4j.evolution.EvolutionFeatureMode.EvolutionFeatureMode
import com.logisima.play.neo4j.evolution.EvolutionFeatureMode

/**
 * Neo4j plugin to stop server.
 *
 * @author : bsimard
 */
class Neo4jPlugin(app: play.api.Application) extends Plugin  with HandleWebCommandSupport {

  /**
   * Init the module when application starting.
   */
  override def onStart{
    Logger.debug("[Neo4jPlugin]: Starting neo4j plugin")
    Neo4j.start()
    val evolutionMode :EvolutionFeatureMode = EvolutionFeatureMode.withName(Play.configuration.getString("neo4j.evolution").getOrElse("disable"))
    Logger.debug("[Neo4jPlugin]: Evolution mode is " + evolutionMode.toString)
    if(evolutionMode != EvolutionFeatureMode.disable) {
      Neo4jEvolutionService.checkEvolutionState(evolutionMode)
    }
  }

  /**
   * Shutdown module when application is stopping.
   */
  override def onStop(){
    Logger.debug("Stopping neo4j plugin")
    Neo4j.stop()
  }

  /**
   * Handle play action from Neo4jInvalidRevision.
   *
   * @param request
   * @param sbtLink
   * @param path
   * @return
   */
  def handleWebCommand(request: play.api.mvc.RequestHeader, sbtLink: play.core.SBTLink, path: java.io.File): Option[play.api.mvc.SimpleResult] = {
    val applyEvolutions = """/@neo4j/evolution/""".r
    lazy val redirectUrl = request.queryString.get("redirect").filterNot(_.isEmpty).map(_(0)).getOrElse("/")

    request.path match {
      case applyEvolutions() => {
        Some {
          Neo4jEvolutionService.checkEvolutionState(EvolutionFeatureMode.auto)
          sbtLink.forceReload()
          play.api.mvc.Results.Redirect(redirectUrl)
        }
      }
      case _ => None
    }
  }
}
