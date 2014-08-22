package com.xebia.innovationday.eventsourcing.loadtest

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Settings {
  private val config = ConfigFactory.load(getClass.getClassLoader, "load-test").getConfig("innovationday.loadtest")

  val baseUrl = config.getString("baseUrl")
  val warmUpUrl = config.getString("warmUpUrl")

  object Pauses {
    val min = config.getInt("pauses.minSeconds").seconds
    val max = config.getInt("pauses.maxSeconds").seconds
  }
}
