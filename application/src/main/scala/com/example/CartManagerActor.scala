package com.example

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.contrib.pattern.ShardRegion.Passivate
import util._
object CartManagerActor {
  def props(cartProps: Props) = Props(new CartManagerActor(cartProps))

}
 
class CartManagerActor(shoppingCartProps: Props) extends Actor with ActorContextCreationSupport with ActorLogging {
  
  import RequestMessages._
  override def receive: Receive = {
    case Envelope(sessionId, payload) =>
      getOrCreateChild(shoppingCartProps, sessionId) forward payload
    case Passivate(calmDownMessage) =>
      sender ! calmDownMessage
  }

}
