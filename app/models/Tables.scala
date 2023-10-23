package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends Tables {
  val profile = slick.jdbc.PostgresProfile
}

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for
  // tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Users.schema

  /** Entity class storing rows of table Users
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param uuid Database column uuid SqlType(uuid)
   *  @param email Database column email SqlType(varchar), Length(255,true)
   *  @param password Database column password SqlType(varchar)
   *  @param activated Database column activated SqlType(bool)
   *  @param termsConfirmed Database column terms_confirmed SqlType(bool)
   *  @param createdAt Database column created_at SqlType(timestamptz)
   *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class UsersRow(id: Long, uuid: java.util.UUID, email: String, password: String, activated: Boolean, termsConfirmed: Boolean, createdAt: java.sql.Timestamp, updatedAt: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Long], e1: GR[java.util.UUID], e2: GR[String], e3: GR[Boolean], e4: GR[java.sql.Timestamp], e5: GR[Option[java.sql.Timestamp]]): GR[UsersRow] = GR{
    prs => import prs._
    (UsersRow.apply _).tupled((<<[Long], <<[java.util.UUID], <<[String], <<[String], <<[Boolean], <<[Boolean], <<[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * = ((id, uuid, email, password, activated, termsConfirmed, createdAt, updatedAt)).mapTo[UsersRow]
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(uuid), Rep.Some(email), Rep.Some(password), Rep.Some(activated), Rep.Some(termsConfirmed), Rep.Some(createdAt), updatedAt)).shaped.<>({r=>import r._; _1.map(_=> (UsersRow.apply _).tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8)))}, (_:Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column uuid SqlType(uuid) */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid")
    /** Database column email SqlType(varchar), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column password SqlType(varchar) */
    val password: Rep[String] = column[String]("password")
    /** Database column activated SqlType(bool) */
    val activated: Rep[Boolean] = column[Boolean]("activated")
    /** Database column terms_confirmed SqlType(bool) */
    val termsConfirmed: Rep[Boolean] = column[Boolean]("terms_confirmed")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("updated_at", O.Default(None))

    /** Uniqueness Index over (email) (database name users_email_key) */
    val index1 = index("users_email_key", email, unique=true)
    /** Uniqueness Index over (uuid) (database name users_uuid_key) */
    val index2 = index("users_uuid_key", uuid, unique=true)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
