package com.example

import java.util.UUID

import akka.actor.{ ActorLogging, PoisonPill, ReceiveTimeout, Props }
import akka.persistence._

import scala.concurrent.duration._

object PersistentShoppingCartActor {
  def props(productRepo: ProductRepo) = Props[PersistentShoppingCartActor](new PersistentShoppingCartActor(productRepo))
  sealed trait Event
  case class ItemAddedEvent(itemId: String) extends Event
  case class ItemRemovedEvent(itemId: String) extends Event
  case class CartCheckedoutEvent(orderId: UUID) extends Event

  case class CartItems(items: Seq[ShoppingCartItem] = Seq()) {
    def update(item: Device) = {
      val updatedItem = items.find(_.item.id == item.id)
        .map(item => item.copy(count = (item.count + 1)))
        .getOrElse(ShoppingCartItem(item))
      copy(items = (items.filterNot(_.item.id == item.id) :+ updatedItem))
    }
    def remove(itemId: String) = {
      copy(items = items.filterNot(_.item.id == itemId))
    }

    def clear() = {
      copy(items = Seq())
    }
    def size = items.size
    def isEmpty = items.isEmpty
  }

}

class PersistentShoppingCartActor(productRepo: ProductRepo) extends PersistentActor with ActorLogging {
  import PersistentShoppingCartActor._

  override def persistenceId = context.self.path.name

  val receiveTimeout: FiniteDuration = 20 seconds

  var cartItems = CartItems()

  def updateState(event: Event): Unit = {
    event match {
      case ItemAddedEvent(itemId) =>
        cartItems = cartItems.update(productRepo.productMap(itemId))
      case ItemRemovedEvent(itemId) =>
        cartItems = cartItems.remove(itemId)
      case CartCheckedoutEvent(_) =>
        cartItems = cartItems.clear()
    }
  }

  val receiveRecover: Receive = {
    case e: Event =>
      log.info(s"recovery: got $e")
      updateState(e)
    case t: RecoveryCompleted =>
      log.info(s"recovery completed; setting receive timeout")
      context.setReceiveTimeout(receiveTimeout)
    case SnapshotOffer(_, shoppingCartState: CartItems) =>
      log.info(s"recovery: got snapshot: ${shoppingCartState.size} items")
      cartItems = shoppingCartState
  }

  val dying: Receive = {
    case SaveSnapshotAndDie => { log.info("saving snapshot"); saveSnapshot(cartItems); log.info("saved snapshot") }
    case SaveSnapshotSuccess(_) => { log.info("farewell cruel world!"); self ! PoisonPill }
    case SaveSnapshotFailure(_, _) => { log.info("Could not save snapshot!"); self ! PoisonPill }
  }

  val receiveCommand: Receive = {
    case ReceiveTimeout => {
      log.info("received timeout")
      context.become(dying)
      self ! SaveSnapshotAndDie
    }

    case AddToCartRequest(itemId) => {
      doWithItem(itemId) { item =>
        persist(ItemAddedEvent(itemId)) { evt =>
          updateState(evt)
          sender ! cartItems
        }
      }
    }
    case RemoveFromCartRequest(itemId) => {
      doWithItem(itemId) { item =>
        persist(ItemRemovedEvent(itemId)) { evt =>
          updateState(evt)
          sender ! cartItems
        }
      }
    }
    case GetCartRequest => {
      sender ! cartItems
    }
    case OrderRequest => {
      if (cartItems.isEmpty) {
        sender ! OrderProcessingFailed
      } else {
        //call order services to order
        val orderId: UUID = UUID.randomUUID()
        persist(CartCheckedoutEvent(orderId)) { evt =>
          updateState(evt)
          saveSnapshot(cartItems)
          sender ! OrderProcessed(orderId.toString)
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