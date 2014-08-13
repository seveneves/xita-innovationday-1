package com.example

import spray.json.DefaultJsonProtocol

case class AddToCartRequest(productId: String)

object AddToCartRequest extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(AddToCartRequest.apply)
}
case class RemoveFromCartRequest(productId: String) {
}
object RemoveFromCartRequest extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(RemoveFromCartRequest.apply)
}

case class GetCartRequest() 

case class ShoppingCartItem(item: Device, count: Int)

object ShoppingCartItem extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(ShoppingCartItem.apply)
}

case class RequestContext[T](sessionId:String, t:T)
