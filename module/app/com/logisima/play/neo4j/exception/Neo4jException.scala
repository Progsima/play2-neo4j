package com.logisima.play.neo4j.exception

/**
 * Classes that handle neo4j exception.
 *
 * @author : bsimard
 */
class Neo4jError(code:String, message:String)
class Neo4jException(messages :Seq[Neo4jError]){}
