package com.tomshley.hexagonal.utils

import com.typesafe.config.Config
import org.apache.pekko.actor.typed.ActorSystem

trait ConfigKeyUtil {
  def config(implicit systemOption: Option[ActorSystem[?]] = Option.empty): Config = {
    systemOption match
      case Some(value) => systemOption.get.settings.config
      case None =>
        import com.typesafe.config.ConfigFactory
        ConfigFactory
          .load()
  }
  def getValueWithDefault[T](keyName:String, defaultValue:Option[T]) : Option[T] = {
    if (config.hasPathOrNull(keyName)) {     
      if (config.getIsNull(keyName)) {  
        defaultValue     
      } else {  
        Some(config.getValue(keyName).unwrapped().asInstanceOf[T])     
      } 
    } else {
      None
    }
  }
}
