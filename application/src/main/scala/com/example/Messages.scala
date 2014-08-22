package com.example

import spray.json.DefaultJsonProtocol

object CartMessages {
  case class AddToCartRequest(itemId: String)
  case class RemoveFromCartRequest(itemId: String)
  case object GetCartRequest
  case class ShoppingCartItem(item: Device, count: Int = 1)
  case object OrderRequest
  case class OrderStateResponse(state: String, orderId: Option[String] = None)
  object AddToCartRequest extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(AddToCartRequest.apply)
  }
  object RemoveFromCartRequest extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(RemoveFromCartRequest.apply)
  }
  object ShoppingCartItem extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(ShoppingCartItem.apply)
  }
  object OrderStateResponse extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(OrderStateResponse.apply)
  }

}

object OrderMessages {
  sealed trait OrderState
  case class OrderProcessed(orderId: String) extends OrderState
  case object OrderProcessingFailed extends OrderState
}

object RequestMessages {
  case class RequestContext[T](sessionId: String, t: T)
}




