package utils

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

object InMemoryDatabaseFlatSpec {
  private val mode = "POSTGRESQL"
  private val inMemoryDatabaseConfiguration: Map[String, Any] = Map(
    "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> s"jdbc:h2:mem:play;MODE=${mode};DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE",
    "slick.dbs.default.db.user" -> "",
    "slick.dbs.default.db.password" -> ""
  )
  // TODO mem:play vs mem:test
}

abstract class InMemoryDatabaseFlatSpec
    extends PlaySpec
    with GuiceOneAppPerSuite {

  import InMemoryDatabaseFlatSpec._

  override def fakeApplication(): Application = {
    val builder =
      new GuiceApplicationBuilder().configure(inMemoryDatabaseConfiguration)
    builder.build()
  }
}
