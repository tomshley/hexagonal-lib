package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.marshalling

import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.util.TimeUtils
import org.json4s.{CustomSerializer, JString}

import java.io.File
import java.nio.file.{Path, Paths}
import scala.reflect.{ClassTag, classTag}

package object serializers {
  final class DateTimeSerializer extends CustomSerializer[TimeUtils.DateTime](format => ( {
    case JString(dateTimeValue) => TimeUtils.DateTime.parse(dateTimeValue)
  }, {
    case dateTime: TimeUtils.DateTime => JString(dateTime.toString())
  }
  ))
  final class AbsPathFileSerializer extends CustomSerializer[File](format => ( {
    case JString(absolutePath) => Paths.get(absolutePath).toFile
  }, {
    case file: File => JString(file.toPath.toAbsolutePath.toString)
  }
  ))

  final class AbsPathSerializer extends CustomSerializer[Path](format => ( {
    case JString(absolutePath) => Paths.get(absolutePath)
  }, {
    case path: Path => JString(path.toAbsolutePath.toString)
  }
  ))

  /**
   * @param classTag$E$0
   * @tparam E
   * @note For scala 3, enum SimpleEnum extends Enum[SimpleEnum]:
   */
  final class JavaEnumNameSerializer[E <: java.lang.Enum[E] : ClassTag] extends CustomSerializer[E](format => ( {
    case JString(value) => {
      val myEnumFind = classTag[E].runtimeClass.getEnumConstants.asInstanceOf[Array[E]].find(_.toString == value)
      myEnumFind.get
    }

  }, {
    case myEnum: E => {

      JString(myEnum.toString)
    }
  }
  ))
}
