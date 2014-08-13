package com.example

import akka.actor.ActorLogging
import akka.actor.Actor

class ShoppingCartActor(productRepo: ProductRepo) extends Actor with ActorLogging with SessionRepo {

  override def receive: Receive = {
    case RequestContext(sessionId, AddToCartRequest(product)) => 
      cartContent(sessionId)
    case RequestContext(sessionId, RemoveFromCartRequest(product)) => 
       cartContent(sessionId)
    case RequestContext(sessionId, GetCartRequest()) => cartContent(sessionId)
  }
  
  def cartContent(sessionId:String) = sender ! productRepo.products.take(3).map(p => ShoppingCartItem(p, 1)).toList

}