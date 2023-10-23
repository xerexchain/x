package dao

import models.Tables.Users
import models.Tables.UsersRow
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
import java.util.UUID.randomUUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class UserDao @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {

  def getByEmail(email: String): Future[Option[UsersRow]] = {
    val query = Users.filter(_.email === email)

    db.run(query.result).map(_.headOption)
  }

  def exists(email: String): Future[Boolean] = {
    getByEmail(email).map(_.nonEmpty)
  }

  def create(
      email: String,
      hashedPassword: String
  ): Future[Int] = {
    val action = Users += UsersRow(
      id = 1,
      uuid = randomUUID,
      email = email,
      password = hashedPassword,
      termsConfirmed = true,
      activated = false,
      createdAt = new Timestamp(System.currentTimeMillis())
    )
    // TODO timestamp with timezone in UTC

    db.run(action)
  }
}
