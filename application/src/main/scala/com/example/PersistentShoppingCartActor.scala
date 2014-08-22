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
    case AddToCartRequest(itemId) => {
      ???
    }
    case RemoveFromCartRequest(itemId) => {
      ???
    }
    case GetCartRequest => {
      ???
    }
    case OrderRequest => {
      ???
    }

  }
}