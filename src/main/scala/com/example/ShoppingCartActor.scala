package com.example

import akka.actor.ActorLogging
import akka.actor.Actor

class ShoppingCartActor(productRepo: ProductRepo) extends Actor with ActorLogging with SessionRepo {

  override def receive: Receive = {
    case RequestContext(sessionId, AddToCartRequest(itemId)) =>
      doWithItem(itemId) { item =>
        val items = upsertCart(sessionId, item)
        sender ! items
      }
    case RequestContext(sessionId, RemoveFromCartRequest(itemId)) =>
      doWithItem(itemId) { item =>
        val items = removeFromCart(sessionId, item)
        sender ! items
      }
    case RequestContext(sessionId, GetCartRequest()) =>
      sender ! sessionState.get(sessionId).getOrElse(Seq())
  }

  private def doWithItem(itemId: String)(item: Device => Unit) = {
    val device = productRepo.productMap.get(itemId) match {
      case Some(device) => item(device)
      case None => sender ! akka.actor.Status.Failure(new IllegalArgumentException(s"Product with id $itemId not found."))
    }
  }

}