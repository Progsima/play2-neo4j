package com.logisima.play.neo4j.utils

import java.io.File
import play.Logger

/**
 * Util class for files.
 *
 * @author : bsimard
 */
object FileUtils {

  /**
   * Retrieve a file from file system by its path.
   *
   * @param path File system path of the file
   * @return
   */
  def getFile(path :String) :Option[File] = {
    Logger.debug("[FileUtils]: Try to get file :" + path)
    val file = new File(path)
     file.exists() match {
      case true => Some(file)
      case _ => None
    }
  }

}
