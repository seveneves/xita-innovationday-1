package com.example

import akka.actor.{PoisonPill, ReceiveTimeout, ActorLogging}
import akka.contrib.pattern.ShardRegion.Passivate
import akka.persistence.{RecoveryCompleted, PersistentActor, SnapshotOffer}
import EventDomain._
import java.util.UUID

import scala.concurrent.duration._

class PersistentShoppingCartActor(productRepo: ProductRepo) extends PersistentActor with ActorLogging {

  override def persistenceId = "cart-id-1"

  val receiveTimeout: FiniteDuration = 2 seconds

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
      case CartCheckedoutEvent(_) =>
        state = Seq[ShoppingCartItem]()
    }
  }

  override def postStop(): Unit = {
    log.info("Saving snapshot...")
    saveSnapshot(state)
  }
  val receiveRecover: Receive = {
    case e:Event =>
      log.info(s"recovery: got $e")
      updateState(e)
    case t:RecoveryCompleted =>
      log.info(s"recovery completed; setting receive timeout")
      context.setReceiveTimeout(receiveTimeout)
    case SnapshotOffer(_, shoppingCartState: Seq[ShoppingCartItem]) =>
      log.info(s"recovery: got snapshot $shoppingCartState")
      state = shoppingCartState
  }

  val receiveCommand: Receive = {
    case ReceiveTimeout => context.parent ! Passivate(PoisonPill)

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
        val orderId: UUID = UUID.randomUUID()
        persist(CartCheckedoutEvent(orderId)) { evt =>
          updateState(evt)
          saveSnapshot(state)
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