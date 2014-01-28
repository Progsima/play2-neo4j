package com.logisima.play.neo4j.utils

import com.logisima.play.neo4j.service.Neo4jTransactionalService
import com.logisima.play.neo4j.Neo4j
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Some helper function for Neo4j.
 */
object Neo4jUtils {

  /**
   * Delete all node & relation of the graph database.
   * Becarefull, there is no limitation, and this operation is a blocking one.
   * This function was design for test.
   */
  def reset(){
    val query :String = "MATCH n-[r?]-() DELETE n,r"
    val api = new Neo4jTransactionalService(Neo4j.serverUrl)
    Await result(api.cypher(query), Duration.Inf)
  }

}
