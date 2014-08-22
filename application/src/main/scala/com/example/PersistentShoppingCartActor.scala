package com.example

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import akka.persistence.SnapshotOffer
import EventDomain._
import java.util.UUID
class PersistentShoppingCartActor(productRepo: ProductRepo) extends PersistentActor with ActorLogging {

  override def persistenceId = "cart-id-1"

  var state = Seq[ShoppingCartItem]()

  def updateState(event: Event): Unit = {
    event match {
      case ItemAddedEvent(itemId) =>
        val updatedItem = state.find(_.item.id == itemId)
          .map(item => item.copy(count = (item.count + 1)))
          .getOrElse(ShoppingCartItem(productRepo.productMap(itemId)))
        state = state.filterNot(_.item.id == itemId) :+ updatedItem
      case ItemRemovedEvent(itemId) => 
        state = state.filterNot(_.item.id == itemId)
      case CartCheckedoutEvent => 
        state = Seq[ShoppingCartItem]()
    } 
  }

  val receiveRecover: Receive = ???

  val receiveCommand: Receive = {
    case AddToCartRequest(itemId) => {
      doWithItem(itemId) { item =>
        persist(ItemAddedEvent(itemId)) { evt =>
          updateState(evt)
          sender ! state
        }
      }
    }
    case RemoveFromCartRequest(itemId) => {
      doWithItem(itemId) { item =>
        persist(ItemRemovedEvent(itemId)) { evt =>
          updateState(evt)
          sender ! state
        }
      }
    }
    case GetCartRequest => {
      sender ! state
    }
    case OrderRequest => {
      if (state.isEmpty) {
        sender ! OrderProcessingFailed
      } else {
        //call order services to order
        persist(CartCheckedoutEvent) { evt =>
          updateState(evt)
          sender ! OrderProcessed(UUID.randomUUID().toString)
        }
      }
    }
  }

  private def doWithItem(itemId: String)(item: Device => Unit) = {
    val device = productRepo.productMap.get(itemId) match {
      case Some(device) => item(device)
      case None => sender ! akka.actor.Status.Failure(new IllegalArgumentException(s"Product with id $itemId not found."))
    }
  }
}