package com.logisima.play.neo4j.utils

import com.logisima.play.neo4j.Neo4j
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.logisima.play.neo4j.service.Neo4jEvolutionService
import com.logisima.play.neo4j.item.EvolutionFeatureMode

/**
 * Some helper function for Neo4j.
 */
object Neo4jUtils {

  /**
   * Delete all node & relation of the graph database.
   * NB: there is no limitation, and this operation is a blocking one.
   * This function was designed for test.
   */
  def deleteAll(){
    val query :String = "MATCH (n) OPTIONAL MATCH n-[r]-() DELETE n,r"
    Await result(Neo4j.cypher(query), Duration.Inf)
  }

  /**
   * Reset the database by deleting all nodes, and populate it with evolutions script.
   * This function was designed for test.
   */
  def reset(){
    // delete the entire database
    Neo4jUtils.deleteAll()
    // populate the database by running evolution script
    Neo4jEvolutionService.checkEvolutionState(EvolutionFeatureMode.auto)
  }

}
