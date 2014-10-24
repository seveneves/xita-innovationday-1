package com.example

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.contrib.pattern.ShardRegion.Passivate
import util._
import akka.contrib.pattern.ClusterSharding
object CartManagerActor {
  //def props(cartProps: Props) = Props(new CartManagerActor(cartProps))
  def props() = Props(new CartManagerActor())

}

//class CartManagerActor(shoppingCartProps: Props) extends Actor with ActorContextCreationSupport with ActorLogging {
class CartManagerActor extends Actor with ActorContextCreationSupport with ActorLogging {

  import RequestMessages._
  import PersistentCartActor._
  override def receive: Receive = {
    //    case Envelope(sessionId, payload) => getOrCreateChild(shoppingCartProps, sessionId) forward payload
    case env @ Envelope(sessionId, payload) => cartActor forward env
    case Passivate(calmDownMessage) =>
      sender ! calmDownMessage
  }

  private[example] def cartActor = ClusterSharding(context.system).shardRegion(shardName)

}
