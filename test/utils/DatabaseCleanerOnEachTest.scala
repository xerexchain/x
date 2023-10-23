package utils

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import utils.extensions._

import scala.util.Try

trait DatabaseCleanerOnEachTest
    extends PlaySpec
    with HasDatabaseConfigProvider[JdbcProfile]
    with BeforeAndAfterEach {

  import profile.api._

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    clearDatabase()
  }

  override protected def afterEach(): Unit = {
    clearDatabase()
    super.afterEach()
  }

  def clearDatabase(): Unit = {
    Try(dropTables())
    createTables()
  }

  private def createTables(): Unit = {
    models.Tables.schema.createStatements.toList.foreach { query =>
      db.run(sqlu"#$query").awaitForResult
      // TODO replace with this one:
      // db.run(SQLActionBuilder(query, SetUnit).asUpdate).awaitForResult
    }
  }

  private def dropTables(): Unit = {
    models.Tables.schema.dropStatements.toList.reverse.foreach { query =>
      db.run(sqlu"#$query").awaitForResult
      // TODO replace with this one:
      // db.run(SQLActionBuilder(query, SetUnit).asUpdate).awaitForResult
    }
  }

}
