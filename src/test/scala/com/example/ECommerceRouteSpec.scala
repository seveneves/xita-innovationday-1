package com.example

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import akka.actor.ActorRef
import akka.testkit.TestActor
import akka.testkit.TestProbe
import spray.http.StatusCodes._
import spray.testkit.Specs2RouteTest
import spray.routing.MissingCookieRejection
import spray.http.HttpCookie
import spray.http.HttpHeaders.Cookie
import spray.httpx.SprayJsonSupport._
import spray.routing._
@RunWith(classOf[JUnitRunner])
class ECommerceRouteSpec extends Specification with Specs2RouteTest with ECommerceRoute {

  def productRepo = ProductRepo()
  def actorRefFactory = system
  override val cartHandler = {
    val probe = TestProbe()
    probe.setAutoPilot {
      new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any) = msg match {
          //      case ListProjects =>
          //        sender ! ListBuffer[Project]()
          //        TestActor.KeepRunning
          //      case AddProject(name, url) =>
          //        sender ! "ok"
          //        TestActor.NoAutoPilot
          case _ =>
            sender ! Seq(ShoppingCartItem(productRepo.products.head, 1))
            TestActor.KeepRunning
        }
      }
    }
    probe.ref
  }
  "MyService" should {

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "GET requests to root must be redirected" in {
      Get("/") ~> myRoute ~> check {
        handled must beTrue
      }
    }

    "GET requests without session cookie must be redirected" in {
      Get("/cart") ~> myRoute ~> check {
        rejection === MissingCookieRejection("session-id")
      }
    }
    "GET requests to cart must return shopping cart" in {
      Get("/cart") ~> Cookie(HttpCookie("session-id", content = "id")) ~> myRoute ~> check {
        status === OK
        responseAs[Seq[ShoppingCartItem]] === Seq(ShoppingCartItem(productRepo.products.head, 1))
      }
    }

    //    "return a MethodNotAllowed error for PUT requests to the root path" in {
    //      Put() ~> sealRoute(myRoute) ~> check {
    //        status === MethodNotAllowed
    //        responseAs[String] === "HTTP method not allowed, supported methods: GET"
    //      }
    //    }
  }

}