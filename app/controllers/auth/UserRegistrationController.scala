package controllers.auth

import dao.UserDao
import forms.UserRegistrationForm.FormErrorWrites
import forms.UserRegistrationForm.userRegistrationForm
import play.api.Logging
import play.api.cache.AsyncCacheApi
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import services.AuthService
import utils.PasswordUtils.hashPassword

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class UserRegistrationController @Inject() (
    cc: ControllerComponents,
    userDao: UserDao,
    cache: AsyncCacheApi
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  protected val authService = new AuthService {}

  def register: Action[AnyContent] = Action.async { implicit request =>
    userRegistrationForm
      .bindFromRequest()
      .fold(
        form => {
          Future.successful(
            BadRequest(Json.obj("errors" -> Json.toJson(form.errors)))
          )
        },
        form => {
          val email = form.email
          val hashedPassword = hashPassword(form.password)

          userDao
            .exists(email)
            .map {
              case true =>
                Future.successful(
                  Conflict(
                    Json.obj(
                      "errors" ->
                        Json.arr(Json.obj("user" -> "User already exists."))
                    )
                  )
                )
              case _ => {
                userDao
                  .create(email, hashedPassword)
                  .map { _ =>
                    val otp = authService.generateOtp(6)
                    authService.storeOtp(cache, email, otp)
                    // TODO Handle the failure in the case of storing the otp or sending it.
                    authService.sendOtp(email, otp)

                    Created(Json.obj("message" -> "Sent activation code."))
                  }
              }
            }
            .recover { case e =>
              logger.error("Error while checking user exists", e)
              Future.successful(serverError)
            }
            .flatMap(identity)
            .recover { case e =>
              logger.error("Error while flatMap Future", e)
              serverError
            }
        }
      )
  }

  private val serverError =
    InternalServerError(
      Json.obj(
        "errors" -> Json.arr(
          Json.obj(
            "server" -> "Something went wrong."
          )
        )
      )
    )
}
