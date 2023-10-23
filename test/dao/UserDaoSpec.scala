package dao

import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import play.api.db.slick.DatabaseConfigProvider
import utils.DatabaseCleanerOnEachTest
import utils.FakeApp
import utils.extensions._

class UserDaoSpec extends DatabaseCleanerOnEachTest with FakeApp {

  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global

  override lazy val dbConfigProvider: DatabaseConfigProvider =
    injector.instanceOf[DatabaseConfigProvider]
  private val userDao = injector.instanceOf[UserDao]

  val email = "test@example.com"
  val passwordHash = "testPasswordHash"

  "getByEmail" must {
    "return the user with the given email if exists" in {
      val numRowsInserted = userDao
        .create(email, passwordHash)
        .awaitForResult

      numRowsInserted mustBe 1

      val user = userDao.getByEmail(email).awaitForResult

      user.map(_.email mustBe email)
    }

    "return None if the user doesn't exist" in {
      val user = userDao.getByEmail(email).awaitForResult
      user mustBe None
    }
  }

  "exists" must {
    "return true if the user with the given email exists" in {
      val numRowsInserted = userDao
        .create(email, passwordHash)
        .awaitForResult

      numRowsInserted mustBe 1

      val exists = userDao.exists(email).awaitForResult

      exists mustBe true
    }

    "return false if the user doesn't exist" in {
      val exists = userDao.exists(email).awaitForResult

      exists mustBe false
    }
  }

  "create" must {
    "increment column id of user table" in {
      userDao
        .create(email, passwordHash)
        .awaitForResult

      val secondEmail = "test2@example.com"
      userDao
        .create(secondEmail, passwordHash)
        .awaitForResult

      val secondUser = userDao.getByEmail(secondEmail).awaitForResult

      secondUser.get.id mustBe 2
      secondUser.get.email mustBe secondEmail
    }

    "reject creating a user with a duplicate email" in {
      userDao
        .create(email, passwordHash)
        .awaitForResult
      val numRowsInserted = userDao
        .create(email, passwordHash)

      numRowsInserted.recover { case thrown =>
        thrown mustBe a[JdbcSQLIntegrityConstraintViolationException]
      }
    }
  }
}
