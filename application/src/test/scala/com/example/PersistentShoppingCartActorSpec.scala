package com.example

import spray.testkit.Specs2RouteTest
import org.specs2.mutable.Specification
import akka.actor.actorRef2Scala
import akka.actor.Props
import akka.actor.ActorSystem
import akka.testkit.TestSupport._
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.testkit.TestSupport
import akka.testkit.PersistenceSpec
import akka.testkit.ImplicitSender
import akka.testkit.AkkaSpec

trait DeactivatedTimeConversions extends org.specs2.time.TimeConversions {
  override def intToRichLong(v: Int) = super.intToRichLong(v)
}

class PersistentShoppingCartActorSpec extends AkkaSpec(PersistenceSpec.config("leveldb", "ShoppingCartActorSpec")) with PersistenceSpec with ImplicitSender {
  //class PersistentShoppingCartActorSpec extends Specification with DeactivatedTimeConversions {
  val productRepo = ProductRepo()
  "The CartActor" should {
    "read items" in {
      val cart = system.actorOf(PersistentShoppingCartActor.props(productRepo))
      cart ! GetCartRequest
      expectMsg(Seq())
    }
  }
}

/*
 * //class PersistentShoppingCartActorSpec extends Specification with DeactivatedTimeConversions {
  implicit val timeout = Timeout(5000l)
  val productRepo = ProductRepo()
  "The CartActor" should {
    "read items" in new AkkaPersistentTestkitContext() {

      val reverseActor = system.actorOf(Props(new PersistentShoppingCartActor(productRepo)))

      import akka.pattern.ask

      val future = reverseActor.ask(GetCartRequest)
      val res = Await.result(future, 5 seconds)
      res === Seq()
    }
*/

