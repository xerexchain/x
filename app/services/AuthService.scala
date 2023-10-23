package services

import play.api.cache.AsyncCacheApi

import scala.concurrent.duration.Duration
import scala.util.Random

trait AuthService {
  def generateOtp(n: Int): String = {
    val chars = "0123456789"
    (1 to n).map(_ => chars(Random.nextInt(chars.length))).mkString("")
  }

  private def otpKey(email: String): String = {
    s"otp-${email}"
  }

  def storeOtp(
      cache: AsyncCacheApi,
      email: String,
      otp: String
  ): Unit = {
    cache.set(
      key = otpKey(email),
      value = otp,
      expiration = Duration(2, "minute")
    )
    // TODO handle failure
  }

  def sendOtp(email: String, code: String): Unit = {
    // TODO handle failure
  }
}
