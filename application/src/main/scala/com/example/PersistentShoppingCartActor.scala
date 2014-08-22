package com.example

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import akka.persistence.SnapshotOffer
import EventDomain._
class PersistentShoppingCartActor(productRepo: ProductRepo) extends PersistentActor with ActorLogging {

  override def persistenceId = "cart-id-1"

  var state = Seq[ShoppingCartItem]()

  def updateState(event: Event): Unit = ???

  val receiveRecover: Receive = ???

  val receiveCommand: Receive = {
    case RequestContext(sessionId, AddToCartRequest(itemId)) => {
      ???
    }
    case RequestContext(sessionId, RemoveFromCartRequest(itemId)) => {
      ???
    }
    case RequestContext(sessionId, GetCartRequest()) => {
      ???
    }
    case RequestContext(sessionId, OrderRequest()) => {
      ???
    }

  }
}