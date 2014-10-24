package com.example

import akka.testkit.TestSupport.AkkaTestkitContext
import com.example.CartMessages._
import org.specs2.mutable.Specification
import spray.json.DefaultJsonProtocol._
import spray.json._
class DomainMarshallerSpec extends Specification {
  "Product Repo" should {
    "be initialize correctly" in new AkkaTestkitContext() {
      val repo = ProductRepoExtension(system).productRepo
      repo.products.size must be_>(1)
    }
  }
  "Cart items" should {
    "be serializable" in new AkkaTestkitContext() {
      val repo = ProductRepoExtension(system).productRepo
      val jsonAst = Seq(ShoppingCartItem(repo.products.head, 1)).toJson
      val cart = jsonAst.convertTo[Seq[ShoppingCartItem]]
      cart.size ==== 1
    }
  }
}