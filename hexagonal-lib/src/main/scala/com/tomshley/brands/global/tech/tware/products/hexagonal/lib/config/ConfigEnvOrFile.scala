package com.tomshley.brands.global.tech.tware.products.hexagonal.lib
package config

/*
  credit: https://gist.github.com/DanielaSfregola/603a39c8c5ae7b620aec
 */

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Properties

class ConfigEnvOrFile(fileNameOption: Option[String] = None) {

  private val config: Config = fileNameOption.fold(
    ifEmpty = ConfigFactory.load())(
    file => ConfigFactory.load(file))

  def envOrElseConfig(name: String): String = {
    Properties.envOrElse(
      name.toUpperCase.replaceAll("""\.""", "_"),
      config.getString(name)
    )
  }
}

object ConfigEnvOrFile {
  lazy val config: ConfigEnvOrFile = ConfigEnvOrFile()
}

