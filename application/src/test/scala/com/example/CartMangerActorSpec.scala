package com.example

import akka.actor._
import akka.contrib.pattern.ShardRegion.Passivate
import akka.testkit.TestSupport.AkkaTestkitContext
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import RequestMessages._
import akka.testkit.TestProbe
import akka.testkit.TestActor
@RunWith(classOf[JUnitRunner])
class CartMangerActorSpec extends Specification {

  "Cart manager" should {
    "create new cart actors for new sessions" in new AkkaTestkitContext() {

      //      val cartActorStubProps = Props(new Actor {
      //        override def receive: Receive = {
      //          case m: String => sender ! s"Echoing: $m"
      //        }
      //      }) 

      class Wrapper(target: ActorRef) extends Actor {
        def receive = {
          case x => target forward x
        }
      }

      val probeActorRef = {
        val probe = TestProbe()
        probe.setAutoPilot {
          new TestActor.AutoPilot {
            def run(sender: ActorRef, msg: Any) = msg match {
              case _ =>
                sender ! s"Echoing: $msg"
                TestActor.KeepRunning
            }
          }
        }
        probe.ref
      }

      val probe = new TestProbe(system)
      //val cartActorStubProps = Props(new Wrapper(probe.ref))

      val cartManager = system.actorOf(Props(new CartManagerActor {
        override def cartActor = probe.ref
      }))

      cartManager ! Envelope("aaaaa", "Bla")

      probe.expectMsg(Envelope("aaaaa", "Bla"))
      //probe.expectMsg("Bla")
      //expectMsg("Echoing: Bla")

//      system.actorSelection(cartManager.path.child("*")) ! Identify()
//
//      expectMsgClass(classOf[ActorIdentity]).ref.map(_.path.name) must be equalTo Some("aaaaa")

      cartManager ! Envelope("aaaaa", "Foo")
      cartManager ! Envelope("bbbbb", "Bar")

      probe.expectMsg(Envelope("aaaaa", "Foo"))
      probe.expectMsg(Envelope("bbbbb", "Bar"))
//      probe.expectMsg("Foo")
//      probe.expectMsg("Bar")
      //      expectMsg("Echoing: Foo")
      //      expectMsg("Echoing: Bar")

/*
      system.actorSelection(cartManager.path.child("*")) ! Identify()

      val ids = Set(expectMsgClass(classOf[ActorIdentity]), expectMsgClass(classOf[ActorIdentity])).map(_.ref.map(_.path.name))
      ids must be equalTo Set(Some("aaaaa"), Some("bbbbb"))
*/

    }

    "reply with passivation content back to sender" in new AkkaTestkitContext() {

      val probe = TestProbe()
      val cartManager = system.actorOf(Props(new CartManagerActor {
        override def cartActor = probe.ref
      }))

      cartManager ! Passivate("Content")
      expectMsg("Content")

    }
  }

}