package com.logisima.play.neo4j.service

import play.api.libs.{Collections, Codecs}
import play.Play
import com.logisima.play.neo4j.utils.FileUtils
import java.io.File
import scala.io.Source
import com.logisima.play.neo4j.evolution.ScriptType._
import com.logisima.play.neo4j.evolution.{ScriptType, Evolution}
import com.logisima.play.neo4j.evolution.ScriptType.ScriptType

/**
 * Neo4j service that handle evolution script.
 *
 * @author : bsimard
 */
class Neo4jEvolutionService(rootUrl: String) {

  /**
   * String interpolation to construct evolution script relative path
   * @param revision
   * @return
   */
  private def evolutionsFilename(path :String, revision: Int, style :ScriptType): String = s"${path}/conf/evolutions/neo4j/${revision}_${style}.cql"

  /**
   * Retrieve evolutions from neo4j database that has been apply.
   *
   * @return
   */
  def neo4jEvolutions() :Seq[Evolution] = {
    Seq.apply(new Evolution(1))
  }

  /**
   * Retrieve evolutions from the application.
   *
   * @return
   */
  def applicationEvolutions() :Seq[Evolution] = {
    Collections.unfoldLeft(1) {
      revision => {
        FileUtils.getFile(evolutionsFilename(Play.application().path().getPath, revision, ScriptType.up)) match {
          case file :File => {
            val upScript :String = Source.fromFile(file).getLines().toString
            val downScript :String = FileUtils.getFile(evolutionsFilename(Play.application().path().getPath, revision, ScriptType.down)) match {
              case file :File => Source.fromFile(file).getLines().toString
              case _ => ""
            }
            Option((revision + 1, Evolution(revision, upScript, downScript)))
          }
        }
      }
    }
  }

}
