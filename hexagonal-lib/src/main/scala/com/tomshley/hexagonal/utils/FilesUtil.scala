package com.tomshley.hexagonal.utils

import java.io.File

trait FilesUtil {
  private final val extensionSeparator = "."

  def recursiveFileList(fileFilter: File => Boolean = f => true)(f: File): Seq[File] = {
    lazy val files = f.list()
    lazy val filesData = {
      files match
        case null => Seq()
        case _ => files.toSeq
    }.map(new File(f, _))

    filesData.filter(fileFilter) ++ filesData.filter(_.isDirectory).flatMap(recursiveFileList(fileFilter))
  }

  def nameWithoutExtension(pathAsString: String): String = {
    if (pathAsString.indexOf(extensionSeparator) > 0) pathAsString.substring(0, pathAsString.lastIndexOf(extensionSeparator)) else pathAsString
  }

  def nameAndExtensionPair(pathAsString: String): (String, String) = {
    if (pathAsString.indexOf(extensionSeparator) > 0) {
      val extensionIndex = pathAsString.lastIndexOf(extensionSeparator)
      (pathAsString.substring(0, extensionIndex), pathAsString.substring(extensionIndex, pathAsString.length))
    } else {
      (pathAsString, "")
    }
  }
}
