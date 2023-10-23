import slick.codegen.SourceCodeGenerator

object SlickCodegen {
  def main(args: Array[String]): Unit = {
    val dbUrl = sys.env("POSTGRES_URL")
    val dbUser = sys.env("POSTGRES_USER")
    val dbPass = sys.env("POSTGRES_PASSWORD")
    val jdbcUrl = s"${dbUrl}?user=${dbUser}&password=${dbPass}"

    val outputDir = "./sql/"

    slick.codegen.SourceCodeGenerator.run(
      profile = "slick.jdbc.PostgresProfile",
      jdbcDriver = "org.postgresql.Driver",
      url = jdbcUrl,
      outputDir = outputDir,
      pkg = "models",
      user = Some(dbUser),
      password = Some(dbPass),
      false,
      false
    )
  }
}
