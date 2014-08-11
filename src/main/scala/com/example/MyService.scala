package com.example

import akka.actor.Actor
import spray.routing._
import directives.LogEntry
import spray.http._
import MediaTypes._
import akka.event.Logging
import spray.http.HttpHeaders.Cookie
import java.util.UUID

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService with StaticResources with Api {

  def showPath(req: HttpRequest) = req.method match {
    case HttpMethods.POST => LogEntry("Method = %s, Path = %s, Data = %s" format (req.method, req.uri, req.entity), Logging.InfoLevel)
    case _ => LogEntry("Method = %s, Path = %s" format (req.method, req.uri), Logging.InfoLevel)
  }

  val myRoute =
    logRequest(showPath _) {
      shoppingCartRoutes ~ staticResources
    }
}

// TODO implement your REST Api
trait Api extends HttpService with SessionCookieDirective {
  implicit val myRejectionHandler = RejectionHandler {
    case MissingCookieRejection(cookieName) :: _ =>
      val uuid = UUID.randomUUID().toString()
      setCookie(HttpCookie("session-id", content = uuid)) {
        redirect("/session-id-test", StatusCodes.TemporaryRedirect)
      }
  }
  val shoppingCartRoutes =
    get {
      path("session-id-test-old") {
        val uuid = UUID.randomUUID().toString()
        println(uuid)
        sessionCookie("session-id", uuid) { sessionCookie =>
          complete { s"the session cookie is $sessionCookie" }
        }
      }
      path("session-id-test-ok1") {
        dynamic {
          val uuid = UUID.randomUUID().toString()
          setCookie(HttpCookie("session-id", content = uuid)) {
            complete { s"the session cookie is " }
          }
        }
      }
      path("set-session") {
        dynamic {
          val uuid = UUID.randomUUID().toString()
          setCookie(HttpCookie("session-id", content = uuid)) {
            redirect("/session-id-test", StatusCodes.PermanentRedirect)
          }
        }
      }

      path("session-id-test") {
        cookie("session-id") { sessionCookie =>
          complete { s"the session cookie is " + sessionCookie }
        }
      }
    }
  private def genSessionIdCookie = HttpCookie("session-id", content = UUID.randomUUID().toString());
}

trait SessionCookieDirective extends HttpService {

  def sessionCookie(name: String, newSessionId: => String = UUID.randomUUID().toString()): Directive1[HttpCookie] =
    headerValue(findCookie(name)) | {
      val sessionCookie = HttpCookie(name, content = newSessionId);
      println("generated" + sessionCookie)
      setCookie(sessionCookie)
      headerValue { case _ => Some(sessionCookie) }
    }

  private def genSessionIdCookie(name: String) = HttpCookie(name, content = UUID.randomUUID().toString());
  private def findCookie(name: String): HttpHeader ⇒ Option[HttpCookie] = {
    case Cookie(cookies) => cookies.find(_.name == name)
    case _ => None
  }
}

// Trait for serving static resources
// Sends 404 for 'favicon.icon' requests and serves static resources in 'bootstrap' folder.
trait StaticResources extends HttpService {

  val staticResources =
    get {
      path("") {
        redirect("/index.html", StatusCodes.PermanentRedirect)
      } ~
        path("favicon.ico") {
          complete(StatusCodes.NotFound)
        } ~
        path(Rest) { path =>
          getFromResource("root/%s" format path)
        }
    }
}