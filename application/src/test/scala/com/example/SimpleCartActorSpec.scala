package com.example

import spray.testkit.Specs2RouteTest
import org.specs2.mutable.Specification
import akka.actor.actorRef2Scala
import akka.actor.Props
import akka.testkit.TestSupport._
import akka.testkit.TestSupport
import RequestMessages._
import CartMessages._
import OrderMessages._
import ProductDomain._
class SimpleCartActorSpec extends Specification
  with Specs2RouteTest {

  val productRepo = ProductRepo()
  "The CartActor" should {
    "read items" in new AkkaTestkitContext() {
      val reverseActor = system.actorOf(Props(new SimpleCartActor(productRepo)), "cart-actor")
      import akka.pattern.ask

      reverseActor ! RequestContext("sessionId-1", GetCartRequest)

      expectMsg(Seq())

    }
    "order" in new AkkaTestkitContext() {
      val reverseActor = system.actorOf(Props(new SimpleCartActor(productRepo)), "cart-actor")
      import akka.pattern.ask
      val product = productRepo.products.head
      reverseActor ! RequestContext("sessionId-2", AddToCartRequest(product.id))

      expectMsg(Seq(ShoppingCartItem(product, 1)))

      reverseActor ! RequestContext("sessionId-2", OrderRequest)
      expectMsgClass(classOf[OrderProcessed])
    }
  }
}
