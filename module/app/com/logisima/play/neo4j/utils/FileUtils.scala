package com.logisima.play.neo4j.utils

import java.io.File

/**
 * Utils class for files.
 *
 * @author : bsimard
 */
object FileUtils {

  def getFile(path :String) :Option[File] = {
    val file = new File(path)
     file.exists() match {
      case true => Some(file)
      case _ => None
    }
  }

}
