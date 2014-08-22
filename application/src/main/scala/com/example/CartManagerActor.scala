package com.example

import akka.actor.{Props, Actor}

class CartManagerActor(shoppingCartProps: Props) extends Actor {

  override def receive: Receive = {
    case RequestContext(sessionId, payload) =>
      context.child(sessionId)
        .getOrElse(context.actorOf(shoppingCartProps, sessionId)) forward payload
  }

}
