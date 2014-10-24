package com.example

import akka.actor.{Props, actorRef2Scala}
import akka.testkit.TestSupport._
import com.example.CartMessages._
import com.example.OrderMessages._
import com.example.RequestMessages._
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
class SimpleCartActorSpec extends Specification
  with Specs2RouteTest {

  val productRepo = ProductRepoExtension(system).productRepo

  "The CartActor" should {
    "read items" in new AkkaTestkitContext() {
      val reverseActor = system.actorOf(Props(new SimpleCartActor(productRepo)), "cart-actor")

      reverseActor ! Envelope("sessionId-1", GetCartRequest)

      expectMsg(Seq())

    }
    "order" in new AkkaTestkitContext() {
      val reverseActor = system.actorOf(Props(new SimpleCartActor(productRepo)), "cart-actor")
      val product = productRepo.products.head
      reverseActor ! Envelope("sessionId-2", AddToCartRequest(product.id))

      expectMsg(Seq(ShoppingCartItem(product, 1)))

      reverseActor ! Envelope("sessionId-2", OrderRequest)
      expectMsgClass(classOf[OrderProcessed])
    }
  }
}
