package controllers.auth

import akka.actor.ActorSystem
import controllers.auth.UserRegistrationController
import dao.UserDao
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.PlaySpec
import play.api.cache.AsyncCacheApi
import play.api.http.Status.BAD_REQUEST
import play.api.http.Status.CONFLICT
import play.api.http.Status.CREATED
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.contentAsJson
import play.api.test.Helpers.contentType
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.status
import play.api.test.Helpers.stubControllerComponents
import services.AuthService
import utils.DatabaseCleanerOnEachTest
import utils.InMemoryDatabaseFlatSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class UserRegistrationControllerSpec extends PlaySpec with MockFactory {

  implicit val ec: ExecutionContext = ExecutionContext.global

  // TODO Is this necessary?
  implicit val sys: ActorSystem = ActorSystem("UserRegistrationControllerSpec")

  val mockAuthService = mock[AuthService]
  val mockUserDao = mock[UserDao]
  val mockCache = mock[AsyncCacheApi]

  val controller = new UserRegistrationController(
    cc = stubControllerComponents(),
    userDao = mockUserDao,
    cache = mockCache
  ) {
    override protected val authService: AuthService = mockAuthService
  }

  val email = "test@example.com"
  val password = "aA123456"

  def testBadRequest(body: JsObject, expected: JsObject): Unit = {
    val request = FakeRequest().withJsonBody(body)
    val result = controller.register()(request)

    status(result) mustBe BAD_REQUEST
    contentType(result) mustBe Some("application/json")
    contentAsJson(result) mustBe expected
  }

  def testInvalidPassword(body: JsObject): Unit = {
    val expected = Json.obj(
      "errors" ->
        Json.arr(
          Json.obj(
            "password" -> "Password must consist of lowercase letters, uppercase letters, and numbers. Password must be at least 8 characters long."
          )
        )
    )

    testBadRequest(body, expected)
  }

  def testInvalidEmail(body: JsObject): Unit = {
    val expected = Json.obj(
      "errors" ->
        Json.arr(Json.obj("email" -> "error.email"))
    )

    testBadRequest(body, expected)
  }

  "The register action" must {
    "return BadRequest when the request body is empty" in {
      val emptyBody = Json.obj()

      val expected = Json.obj(
        "errors" -> Json.arr(
          Json.obj("email" -> "error.required"),
          Json.obj("password" -> "error.required"),
          Json.obj(
            "terms_confirmed" -> "You must confirm the terms of service."
          )
        )
      )

      testBadRequest(emptyBody, expected)
    }

    "return BadRequest on invalid email address" in {
      val noAtSign = Json.obj(
        "email" -> "test",
        "password" -> password,
        "terms_confirmed" -> true
      )

      testInvalidEmail(noAtSign)

      // TODO should this pass?
      // val noTld = Json.obj(
      //   "email" -> "test@example",
      //   "password" -> password,
      //   "terms_confirmed" -> true
      // )

      // testInvalidEmail(noTld)

      val noPrefix = Json.obj(
        "email" -> "@example",
        "password" -> password,
        "terms_confirmed" -> true
      )

      testInvalidEmail(noPrefix)
    }

    "return BadRequest on invalid password" in {
      val shortPassword = Json.obj(
        "email" -> email,
        "password" -> "1234567",
        "terms_confirmed" -> true
      )

      testInvalidPassword(shortPassword)

      val noLowerCase = Json.obj(
        "email" -> email,
        "password" -> "1234567A",
        "terms_confirmed" -> true
      )

      testInvalidPassword(noLowerCase)

      val noUpperCase = Json.obj(
        "email" -> email,
        "password" -> "1234567a",
        "terms_confirmed" -> true
      )

      testInvalidPassword(noUpperCase)
    }

    "return BadRequest when terms of service isn't confirmed" in {
      val expected = Json.obj(
        "errors" -> Json.arr(
          Json.obj(
            "terms_confirmed" -> "You must confirm the terms of service."
          )
        )
      )

      // TODO should this work?
      // val stringConfirmed = Json.obj(
      //   "email" -> email,
      //   "password" -> password,
      //   "terms_confirmed" -> "true"
      // )

      // testBadRequest(stringConfirmed, expected)

      val notConfirmed = Json.obj(
        "email" -> email,
        "password" -> password,
        "terms_confirmed" -> false
      )

      testBadRequest(notConfirmed, expected)
    }

    "return Conflict when the user already exists" in {
      (mockUserDao.exists _)
        .expects(email)
        .returning(Future.successful(true))
        .once()

      val body = Json.obj(
        "email" -> email,
        "password" -> password,
        "terms_confirmed" -> true
      )
      val request = FakeRequest().withJsonBody(body)
      val result = controller.register(request)

      status(result) mustBe CONFLICT
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj(
        "errors" -> Json.arr(
          Json.obj(
            "user" -> "User already exists."
          )
        )
      )
    }

    "return InternalServerError in case of db failure when checking user exists" in {
      (mockUserDao.exists _)
        .expects(email)
        .returning(Future.failed(new Exception("Some error related to db.")))
        .once()

      val body = Json.obj(
        "email" -> email,
        "password" -> password,
        "terms_confirmed" -> true
      )
      val request = FakeRequest().withJsonBody(body)
      val result = controller.register(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj(
        "errors" -> Json.arr(
          Json.obj(
            "server" -> "Something went wrong."
          )
        )
      )
    }

    // This happens because there is no transaction.
    "return InternalServerError when the user already exists and check by email retured no user" in {
      (mockUserDao.exists _)
        .expects(email)
        .returning(Future.successful(false))
        .once()

      (mockUserDao
        .create(_: String, _: String))
        .expects(
          email,
          * // Can't determine hashedPassword since it's generated from random salt.
        )
        .returning(
          // TODO replace with JdbcSQLIntegrityConstraintViolationException
          Future.failed(new Exception("Some error related to db."))
        )
        .once()

      val body = Json.obj(
        "email" -> email,
        "password" -> password,
        "terms_confirmed" -> true
      )
      val request = FakeRequest().withJsonBody(body)
      val result = controller.register(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj(
        "errors" -> Json.arr(
          Json.obj(
            "server" -> "Something went wrong."
          )
        )
      )
    }

    "return Created when the user is created successfully" in {
      val otp = "123456"

      (mockUserDao
        .exists(_: String))
        .expects(email)
        .returning(Future.successful(false))
        .once()

      (mockUserDao
        .create(_: String, _: String))
        .expects(
          email,
          * // Can't determine hashedPassword since it's generated from random salt.
        )
        .returning(Future.successful(1))
        .once()

      (mockAuthService
        .generateOtp(_: Int))
        .expects(6)
        .returning(otp)
        .once()

      (mockAuthService
        .storeOtp(_: AsyncCacheApi, _: String, _: String))
        .expects(mockCache, email, otp)
        .once()

      (mockAuthService
        .sendOtp(_: String, _: String))
        .expects(email, otp)
        .once()

      val body = Json.obj(
        "email" -> email,
        "password" -> password,
        "terms_confirmed" -> true
      )
      val request = FakeRequest().withJsonBody(body)
      val result = controller.register(request)

      status(result) mustBe CREATED
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj(
        "message" -> "Sent activation code."
      )
    }
  }

}
