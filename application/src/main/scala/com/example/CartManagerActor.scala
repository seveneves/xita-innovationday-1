package com.example

import akka.actor.{ ActorLogging, Props, Actor }
import akka.contrib.pattern.ShardRegion.Passivate

object CartManagerActor {
  def props(cartProps: Props) = Props[CartManagerActor](new CartManagerActor(cartProps))

}

class CartManagerActor(shoppingCartProps: Props) extends Actor with ActorLogging {
  import RequestMessages._
  override def receive: Receive = {
    case RequestContext(sessionId, payload) =>

      context.child(sessionId).getOrElse({
        log.info(s"Creating new shopping cart actor for session $sessionId")
        context.actorOf(shoppingCartProps, sessionId)
      }) forward payload

    case Passivate(calmDownMessage) =>
      sender ! calmDownMessage
  }

}
