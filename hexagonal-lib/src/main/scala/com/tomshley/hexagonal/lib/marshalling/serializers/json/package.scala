package com.tomshley.hexagonal.lib.marshalling.serializers

import com.tomshley.hexagonal.lib.utils.TimeUtils
import org.json4s.{CustomSerializer, JString}

import java.io.File
import java.nio.file.{Path, Paths}
import java.time.ZonedDateTime
import scala.reflect.{ClassTag, classTag}

package object json {
  final class DateTimeSerializer
      extends CustomSerializer[TimeUtils.DateTime](
        format =>
          ({
            case JString(dateTimeValue) =>
              TimeUtils.DateTime.parse(dateTimeValue)
          }, {
            case dateTime: TimeUtils.DateTime => JString(dateTime.toString())
          })
      )
  final class ZonedDateTimeSerializer
      extends CustomSerializer[ZonedDateTime](
        format =>
          ({
            case JString(zonedDateTimeValue) =>
              ZonedDateTime.parse(zonedDateTimeValue)
          }, {
            case zonedDateTimeValue: ZonedDateTime => JString(zonedDateTimeValue.toString)
          })
      )
  final class AbsPathFileSerializer
      extends CustomSerializer[File](
        format =>
          ({
            case JString(absolutePath) => Paths.get(absolutePath).toFile
          }, {
            case file: File => JString(file.toPath.toAbsolutePath.toString)
          })
      )

  final class AbsPathSerializer
      extends CustomSerializer[Path](
        format =>
          ({
            case JString(absolutePath) => Paths.get(absolutePath)
          }, {
            case path: Path => JString(path.toAbsolutePath.toString)
          })
      )

  final class JavaEnumNameSerializer[E <: java.lang.Enum[E]: ClassTag]
      extends CustomSerializer[E](
        format =>
          ({
            case JString(value) =>
              val myEnumFind = classTag[E].runtimeClass.getEnumConstants
                .asInstanceOf[Array[E]]
                .find(_.toString == value)
              myEnumFind.get

          }, {
            case myEnum: E =>

              JString(myEnum.toString)
          })
      )
}
