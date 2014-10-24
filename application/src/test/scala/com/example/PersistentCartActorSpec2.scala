package com.example

import org.specs2.mutable.Specification
import org.specs2.time.{ NoTimeConversions => NTC }
import org.specs2.runner.JUnitRunner
import akka.actor.actorRef2Scala
import akka.actor.Props
import akka.actor.ActorSystem
import akka.testkit.TestSupport._
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import RequestMessages._
import CartMessages._
import OrderMessages._
import ProductDomain._
import akka.testkit.ImplicitSender
import akka.actor.ReceiveTimeout
import akka.actor.Terminated
import org.specs2.mutable.SpecificationLike
import org.specs2.execute.AsResult
import org.specs2.execute.Success
import akka.testkit.TestActorRef
@org.junit.runner.RunWith(classOf[JUnitRunner])
class PersistentCartActorSpec2 extends AkkaPersistentTestkitContext with SpecificationLike {
  sequential
  implicit def anyToSuccess[T]: AsResult[T] = new AsResult[T] {
    def asResult(t: => T) = {
      t
      Success()
    }
  }
  val productRepo = ProductRepoExtension(system).productRepo


  "The CartActor" should {
    val product = productRepo.products.head

    "return carts contents" in {
      val cart = system.actorOf(PersistentCartActor.props())
      cart ! GetCartRequest
      expectMsg(Seq())

      cart ! AddToCartRequest(product.id)
      expectMsg(Seq(ShoppingCartItem(product, 1)))

      cart ! GetCartRequest
      expectMsg(Seq(ShoppingCartItem(product, 1)))
    }
    "snapshot when timing out" in {
      val cart = system.actorOf(PersistentCartActor.props())
      watch(cart)
      cart ! ReceiveTimeout
      expectMsgPF() {
        case Terminated(terminated) => {
          terminated ! GetCartRequest
          expectNoMsg
        }
      }
    }
  }
}

