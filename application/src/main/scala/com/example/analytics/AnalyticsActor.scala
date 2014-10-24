package com.example.analytics

import akka.actor.{Props, Actor}
import java.util.UUID

object AnalyticsActor {
  def name = "analytics-actor"
  def props() = Props[AnalyticsActor]
}

class AnalyticsActor extends Actor {

  val uuid = UUID.randomUUID()

  override def receive: Receive = {
    case m@_ => println(s"analytics actor '$uuid' - message unhandled: $m")
  }

}
