package com.xebia.innovationday.eventsourcing.loadtest

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BasicSimulation extends Simulation {

	private val httpProtocol = http
    .warmUp(Settings.warmUpUrl)
		.baseURL(Settings.baseUrl)
		.inferHtmlResources()
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		//.connection("keep-alive")
		.userAgentHeader("Gatling")
    .maxConnectionsPerHost(1) // limit number of connections, so that the generator can survive...

  private object Headers {
    val html = Map(
      HttpHeaderNames.Accept -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
    )

    val json = Map(
      HttpHeaderNames.Accept -> "application/json, text/plain, */*",
      HttpHeaderNames.ContentType -> "application/json;charset=utf-8"
    )

    val jsonNoCache = Map(
      HttpHeaderNames.CacheControl -> "no-cache",
      HttpHeaderNames.Pragma -> "no-cache"
    ) ++ json
  }

  private val phones = jsonFile("phones.json").random

  /**
   * Instead of following a gazillion redirects, we will fake it and generate our own cookies for now.
   */
  private val addDefaultCookies = exec(
    // TODO UUID will not do for all cases, use SecureRandom instead?
    addCookie(Cookie("session-id", UUID.randomUUID().toString()))
  )

  private val doPause = pause(Settings.Pauses.min, Settings.Pauses.max)

  private def addToCart(itemId: String) = exec(http("Add item to cart")
    .post("/cart")
    .headers(Headers.jsonNoCache)
    .body(StringBody(s"""{"itemId":"$itemId"}""".toString())).asJSON
    .check(status.is(200))
  )

  private def removeItemFromCart(itemId: String) = exec(http("Remove item from cart")
    .delete(s"/cart?itemId=$itemId".toString())
    .headers(Headers.json)
    .check(status.is(200))
  )

  private val placeOrder = exec(http("Place order")
    .put("/order")
    .headers(Headers.json)
    .body(StringBody("""{}""")).asJSON
    .check(status.is(200))
  )

	private val scn = scenario("BasicSimulation").exec(
    addDefaultCookies,

    feed(phones),
    addToCart("${id}"),
    doPause,
    feed(phones),
    addToCart("${id}"),
    doPause,
    removeItemFromCart("${id}"), // remove the last added phone
    doPause,
    placeOrder,

    // Urs wants this twice...
    doPause,
    feed(phones),
    addToCart("${id}"),
    doPause,
    feed(phones),
    addToCart("${id}"),
    doPause,
    removeItemFromCart("${id}"), // remove the last added phone
    doPause,
    placeOrder
  )

	setUp(
    scn.inject(
//      atOnceUsers(1)
      rampUsers(100) over (10 seconds),
      constantUsersPerSec(100) during (1 minutes)
    )
  ).protocols(httpProtocol)
}