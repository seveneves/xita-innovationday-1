package com.example

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import spray.can.Http.Bind
import com.example.analytics.Analytics

trait WebApp extends App {

  implicit val system = ActorSystem("shopping-cart")

  // always start singletons immediately
  Analytics(system)

  //val cartHandlerProps = CartManagerActor.props(PersistentCartActor.props(productRepo))
  
  //start sharding
  ClusterCartManagerActorProvider.startSharding(system)

  
  // create and start our service actor
  val service = system.actorOf(ECommerceActor.props(), "e-commerce-route")
  
  // To run project on Heroku, get PORT from environment
  val httpHost = "0.0.0.0"
  val httpPort = Option(System.getenv("PORT")).getOrElse("8080").toInt

  // create a new HttpServer using our handler tell it where to bind to
  IO(Http) ! Bind(listener= service, interface = httpHost, port=httpPort)

}

object Boot extends App with WebApp {

}
