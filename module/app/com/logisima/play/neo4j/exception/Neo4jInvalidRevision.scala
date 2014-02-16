package com.logisima.play.neo4j.exception

import play.api.PlayException

/**
 * Play Neo4j exception to handle evolution.
 *
 * @param script The cypher script that should be execute (and will be display to the user)
 */
class Neo4jInvalidRevision(script :String) extends PlayException.RichDescription (
  "Your Neo4j database needs evolution!",
  "An cypher script need to be run on your database.") {

    def subTitle = "Evolutions script must be run:"
    def content = script

    private val javascript = """
        document.location = '/@neo4j/evolution/?redirect=' + encodeURIComponent(location)"""
        .format().trim

    def htmlDescription = {
      <span>An Cypher script will be run on your database -</span>
      <input name="evolution-button" type="button" value="Apply this script now!" onclick={ javascript }/>
    }.mkString
}
