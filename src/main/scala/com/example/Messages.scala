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

