package com.example

import java.util.UUID

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorRef
import akka.event.Logging
import akka.util.Timeout
import spray.http._
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing._
import spray.routing.directives.LogEntry

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ECommerceActor(val cartHandler: ActorRef) extends Actor with ECommerceRoute {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait ECommerceRoute extends HttpService with StaticResources with Api {
  val cartHandler: ActorRef
  def showPath(req: HttpRequest) = req.method match {
    case HttpMethods.POST => LogEntry("Method = %s, Path = %s, Data = %s" format (req.method, req.uri, req.entity), Logging.InfoLevel)
    case _ => LogEntry("Method = %s, Path = %s" format (req.method, req.uri), Logging.InfoLevel)
  }

  val myRoute =
    logRequest(showPath _) {
      shoppingCartRoutes ~ staticResources
    }
}

//REST Api
trait Api extends HttpService {
  implicit val timeout = Timeout(20 seconds)
  import akka.pattern.ask
  val cartHandler: ActorRef
  implicit def executionContext = ExecutionContext.global

  implicit val sessionIdGenerationHandler = RejectionHandler {
    case MissingCookieRejection(cookieName) :: _ =>
      setCookie(HttpCookie("session-id", content = UUID.randomUUID().toString())) {
        redirect("/", StatusCodes.TemporaryRedirect)
      }
  }
  import spray.json._
  import DefaultJsonProtocol._
  val shoppingCartRoutes =
    pathPrefix("cart") {
      cookie("session-id") { sessionCookie =>
        val sessionId = sessionCookie.content
        post {
          entity(as[AddToCartRequest]) { addMsg =>
            handleCartRequest(RequestContext(sessionId, addMsg))
          }
        } ~
          delete {
            parameter('itemId) { itemId =>
              handleCartRequest(RequestContext(sessionId, RemoveFromCartRequest(itemId)))
            }
          } ~
          get {
            handleCartRequest(RequestContext(sessionId, GetCartRequest()))
          }
      }
    } ~ path("order") {
      cookie("session-id") { sessionCookie =>
        put {
          handleOrderRequest(sessionCookie.content)
        }
      }
    }
  private def handleCartRequest[T](reqCtx: RequestContext[T]) = {
    val respFuture = cartHandler.ask(reqCtx).mapTo[Seq[ShoppingCartItem]]
    onComplete(respFuture) {
      case Success(res) => complete(res)
      case Failure(e) => completeWithError(e)
    }
  }

  private def handleOrderRequest(sessionId: String) = {
    val processingStateFuture = cartHandler.ask(RequestContext(sessionId, OrderRequest()))
    onComplete(processingStateFuture) {
      case Success(resp) => resp match {
        case ok @ OrderProcessed(orderId) => complete(OrderStateResponse(ok.getClass.getSimpleName(), Some(orderId)))
        case nok => complete(OrderStateResponse(nok.toString))
      }
      case Failure(e) => completeWithError(e)
    }
  }
  private def completeWithError(e: Throwable) = complete(StatusCodes.InternalServerError, e.getMessage())

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