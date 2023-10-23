package utils

import play.api.inject.guice.GuiceApplicationBuilder

trait FakeApp {
  lazy val appBuilder =
    new GuiceApplicationBuilder().build()
  lazy val injector = appBuilder.injector
}
