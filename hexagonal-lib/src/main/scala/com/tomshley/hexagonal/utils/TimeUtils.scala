package com.tomshley.hexagonal.utils

import com.github.nscala_time.time.Implicits

object TimeUtils extends TimeUtils

object TimeUtilsTypes extends TimeUtilsTypes

object TimeUtilsForwarder extends TimeUtilsForwarder

trait TimeUtils extends TimeUtilsTypes with TimeUtilsForwarder with Implicits

trait TimeUtilsTypes {
  type Chronology = org.joda.time.Chronology
  type DateTime = org.joda.time.DateTime
  type DateTimeFormat = org.joda.time.format.DateTimeFormat
  type DateTimeZone = org.joda.time.DateTimeZone
  type Duration = org.joda.time.Duration
  type Interval = org.joda.time.Interval
  type LocalDate = org.joda.time.LocalDate
  type LocalDateTime = org.joda.time.LocalDateTime
  type LocalTime = org.joda.time.LocalTime
  type Period = org.joda.time.Period
  type Partial = org.joda.time.Partial
  type YearMonth = org.joda.time.YearMonth
  type MonthDay = org.joda.time.MonthDay
}

trait TimeUtilsForwarder {
  val DateTime = com.github.nscala_time.time.StaticDateTime
  val DateTimeFormat = com.github.nscala_time.time.StaticDateTimeFormat
  val DateTimeZone = com.github.nscala_time.time.StaticDateTimeZone
  val Duration = com.github.nscala_time.time.StaticDuration
  val Interval = com.github.nscala_time.time.StaticInterval
  val LocalDate = com.github.nscala_time.time.StaticLocalDate
  val LocalDateTime = com.github.nscala_time.time.StaticLocalDateTime
  val LocalTime = com.github.nscala_time.time.StaticLocalTime
  val Period = com.github.nscala_time.time.StaticPeriod
  val Partial = com.github.nscala_time.time.StaticPartial
  val YearMonth = com.github.nscala_time.time.StaticYearMonth
  val MonthDay = com.github.nscala_time.time.StaticMonthDay
}
