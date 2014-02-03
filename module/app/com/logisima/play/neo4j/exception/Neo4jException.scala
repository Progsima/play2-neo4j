package com.logisima.play.neo4j.exception

/**
 * Classes that handle neo4j exception.
 *
 * @author : bsimard
 */
case class Neo4jError(code:String, message:String) {

  override def toString :String  = {
    "[" + code + "] " + message
  }
}

case class Neo4jException(errors :Seq[Neo4jError]) {

  override def toString :String = {
    errors.foldLeft("") {
      (text, error) => error.toString + "\n"
    }
  }
}
