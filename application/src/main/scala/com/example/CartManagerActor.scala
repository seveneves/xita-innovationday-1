package com.example

import akka.actor.{Props, Actor}
import akka.contrib.pattern.ShardRegion.Passivate

class CartManagerActor(shoppingCartProps: Props) extends Actor {

  override def receive: Receive = {
    case RequestContext(sessionId, payload) =>
      context.child(sessionId)
        .getOrElse(context.actorOf(shoppingCartProps, sessionId)) forward payload

    case Passivate(calmDownMessage) =>
      sender ! calmDownMessage
  }

}
