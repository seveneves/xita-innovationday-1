package com.example

import akka.actor.Actor.Receive
import akka.actor._
import akka.testkit.TestProbe
import org.junit.runner.RunWith
import org.specs2.SpecificationLike
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.up.pi.TestSupport.AkkaTestkitContext

@RunWith(classOf[JUnitRunner])
class CartMangerActorSpec extends Specification {

  "Cart manager" should {
    "create new cart actors for new sessions" in new AkkaTestkitContext() {

      val cartActorStubProps = Props(new Actor {
        override def receive: Receive = {
          case m: String => sender ! s"Echoing: $m"
        }
      })

      val cartManager = system.actorOf(Props(new CartManagerActor(cartActorStubProps)))

      cartManager ! RequestContext("aaaaa", "Bla")

      expectMsg("Echoing: Bla")

      system.actorSelection(cartManager.path.child("*")) ! Identify()

      expectMsgClass(classOf[ActorIdentity]).ref.map(_.path.name) must be equalTo Some("aaaaa")

      cartManager ! RequestContext("aaaaa", "Foo")
      cartManager ! RequestContext("bbbbb", "Bar")

      expectMsg("Echoing: Foo")
      expectMsg("Echoing: Bar")

      system.actorSelection(cartManager.path.child("*")) ! Identify()

      expectMsgClass(classOf[ActorIdentity]).ref.map(_.path.name) must be equalTo Some("aaaaa")
      expectMsgClass(classOf[ActorIdentity]).ref.map(_.path.name) must be equalTo Some("bbbbb")

    }
  }



}