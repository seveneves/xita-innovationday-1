package com.example

import spray.json.DefaultJsonProtocol

case class AddToCartRequest(itemId: String)

object AddToCartRequest extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(AddToCartRequest.apply)
}
case class RemoveFromCartRequest(itemId: String) {
}
object RemoveFromCartRequest extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(RemoveFromCartRequest.apply)
}

case object GetCartRequest

case class ShoppingCartItem(item: Device, count: Int = 1)

object ShoppingCartItem extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(ShoppingCartItem.apply)
}

case object OrderRequest

case class OrderStateResponse(state:String, orderId:Option[String] = None)

object OrderStateResponse extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(OrderStateResponse.apply)
}

case class RequestContext[T](sessionId:String, t:T)
