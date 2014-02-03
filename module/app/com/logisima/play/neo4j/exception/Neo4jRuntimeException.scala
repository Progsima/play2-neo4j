package com.logisima.play.neo4j.exception

import play.api.PlayException


/**
 * Generic Neo4j exception for unexpected error cases.
 */
case class Neo4jRuntimeException(code :String, message :String) extends PlayException(code, message)