package com.logisima.play.neo4j.item

/**
 * Enumeration that represent the type of an cypher evlution script, ie <code>up</code> or <code>down</code>.
 *
 * @author : bsimard
 */
object CypherScriptType extends Enumeration {
  type CypherScriptType = Value
  val up, down = Value
}
