package com.example.analytics

import akka.actor.{Props, Actor}

object AnalyticsActor {
  def name = "analytics-actor"
  def props() = Props[AnalyticsActor]
}

class AnalyticsActor extends Actor {

  override def receive: Receive = {
    case m@_ => println(s"message unhandled: $m")
  }

}
