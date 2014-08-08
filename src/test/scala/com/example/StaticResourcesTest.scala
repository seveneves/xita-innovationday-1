package com.example

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes._

class StaticResourcesTest extends FlatSpec with ScalatestRouteTest with ShouldMatchers with StaticResources {
  def actorRefFactory = system

  behavior of "StaticResources routing trait"

  it should "respond with 404 (NotFound) for 'favicon.ico' request" in {
    Get("/favicon.ico") ~> staticResources ~> check {
      handled should be(true)
      status should be(NotFound)
    }
  }

  it should "return a static resource from bootstrap folder" in {
    Get("/bootstrap/css/bootstrap.css") ~> staticResources ~> check {
      handled should be(true)
      status should be(OK)
      entityAs[String] should include(".affix")
    }
  }

  it should "redirect '/' to '/index.html" in {
    Get("/") ~> staticResources ~> check {
      handled should be (true)
      status should be (MovedPermanently)
    }
  }

  it should "return index.html contents from '/index.html' request" in {
    Get("/index.html") ~> staticResources ~> check {
      handled should be(true)
      status should be(OK)
      entityAs[String] should include("<html")
    }
  }
}
