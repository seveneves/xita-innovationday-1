package com.example

import org.specs2.mutable.Specification
import spray.http._
import spray.http.HttpCharsets._
import spray.http.HttpEntity
import spray.http.MediaTypes._
import scala.io.Source
import spray.json._
import DefaultJsonProtocol._
import java.io.File
import scala.collection.JavaConversions._
import RequestMessages._
import CartMessages._
import OrderMessages._
class DomainMarshallerSpec extends Specification {
  "Product Repo" should {
    "be initialize correctly" in {
      val repo = ProductRepo.apply()
      repo.products.size must be_>(1)
    }
  }
  "Cart items" should {
    "be serializable" in {
      val repo = ProductRepo.apply()
      val jsonAst = Seq(ShoppingCartItem(repo.products.head, 1)).toJson
      val cart = jsonAst.convertTo[Seq[ShoppingCartItem]]
      cart.size ==== 1
    }
  }
}