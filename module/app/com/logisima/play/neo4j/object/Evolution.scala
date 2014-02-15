package com.logisima.play.neo4j.evolution

/**
 * A cypher evolution.
 *
 * An evolution includes ‘up’ changes, to upgrade to to the version, as well as ‘down’ changes, to downgrade the database
 * to the previous version.
 *
 * @param revision revision number
 * @param cypher_up the cypher statements for UP application
 * @param cypher_down the cypher statements for DOWN application
 */
case class Evolution(revision: Int, cypher_up: String = "", cypher_down: String = "", hash :String)