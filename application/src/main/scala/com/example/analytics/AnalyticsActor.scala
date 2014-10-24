package com.example.analytics

import akka.actor.{Props, Actor}
import java.util.UUID
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{SubscribeAck, Subscribe}
import AnalyticsActor._

object AnalyticsActor {
  val topic = "cart-event"
  def name = "analytics-actor"
  def props() = Props[AnalyticsActor]
}

class AnalyticsActor extends Actor {
  val uuid = UUID.randomUUID()

  val mediator = DistributedPubSubExtension(context.system).mediator

  override def preStart() = mediator ! Subscribe(topic, None, self)

  override def receive: Receive = {
    case sa@SubscribeAck(_) => println(s"subscribed: '$uuid' - $sa")
    case m@_ => println(s"analytics actor '$uuid' - message unhandled: $m")
  }

}
