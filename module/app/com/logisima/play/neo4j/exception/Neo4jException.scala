package com.logisima.play.neo4j.exception

/**
 * Classes that handle neo4j exception.
 *
 * @author : bsimard
 */
case class Neo4jError(code:String, message:String)
case class Neo4jException(errors :Seq[Neo4jError])
