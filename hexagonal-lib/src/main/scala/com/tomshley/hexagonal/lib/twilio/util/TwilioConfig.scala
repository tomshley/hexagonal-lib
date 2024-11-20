package com.tomshley.hexagonal.lib.twilio.util

import com.twilio.`type`.PhoneNumber

case class TwilioConfig(accountSid:String, authToken:String, from:String) {
  val twilioFrom = PhoneNumber(from)
}
