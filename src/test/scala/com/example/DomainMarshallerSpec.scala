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
class DomainMarshallerSpec extends Specification {
  "Domain" should {
     "be deserializable" in {
      try {
        ProductRepo.apply()
      } catch {
        case e: Exception => failure("unmarshalling must not throw an exception" +  e.getMessage())
      }
      true must beTrue
    }
       "be serializable" in {
      try {
        val repo = ProductRepo.apply()
        val res = Seq(ShoppingCartItem(repo.products.head, 1)).toJson
      } catch {
        case e: Exception => failure("marshalling must not throw an exception" +  e.getMessage())
      }
      true must beTrue
    }
  }
}