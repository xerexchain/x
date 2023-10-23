package forms

import play.api.data.Form
import play.api.data.FormError
import play.api.data.Forms
import play.api.data.validation.Constraint
import play.api.data.validation.Constraints
import play.api.data.validation.Invalid
import play.api.data.validation.Valid
import play.api.data.validation.ValidationError
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes
import utils.PasswordUtils.PasswordPattern

case class UserRegistrationForm(
    email: String,
    password: String,
    termsConfirmed: Boolean
)

object UserRegistrationForm {

  implicit object FormErrorWrites extends Writes[FormError] {
    override def writes(o: FormError): JsValue = Json.obj(
      o.key -> o.message
    )
  }

  val passwordCheckConstraint: Constraint[String] =
    Constraint("constraints.passwordcheck") { plainText =>
      val errors = plainText match {
        case "" => Seq(ValidationError("Password is required."))
        case _ if !PasswordPattern.matches(plainText) =>
          Seq(
            ValidationError(
              """Password must consist of
            | lowercase letters, uppercase letters, and numbers.
            | Password must be at least 8 characters long.""".stripMargin
                .replaceAll("\n", "")
            )
          )
        case _ => Nil
      }

      errors match {
        case Nil => Valid
        case _   => Invalid(errors)
      }
    }

  val userRegistrationForm: Form[UserRegistrationForm] = Form(
    Forms.mapping(
      "email" -> Forms.email.verifying(Constraints.nonEmpty),
      "password" -> Forms.nonEmptyText.verifying(passwordCheckConstraint),
      "terms_confirmed" -> Forms.checked(
        "You must confirm the terms of service."
      )
    )(UserRegistrationForm.apply)(UserRegistrationForm.unapply)
  )
}
