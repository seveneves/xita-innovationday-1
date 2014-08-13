package com.example
import java.io.File
import spray.json.JsonParser
import scala.io.Source
object ProductRepo {

  def apply(): ProductRepo = {
    val prods = new File(getClass.getResource("/root/phones/").toURI())
      .listFiles()
      .filterNot(_.getName() == "phones.json")
      .map { file =>
        val res = Source.fromFile(file).mkString
        val jsonAst = JsonParser(res)
        jsonAst.convertTo[Device]
      }
    new ProductRepoImpl(prods)
  }
}

class ProductRepoImpl(val products: Seq[Device]) extends ProductRepo

trait ProductRepo {
  val products: Seq[Device]

  lazy val productMap: Map[String, Device] = products.map(p => p.id -> p).toMap
}

trait SessionRepo {
  import collection.mutable._
  
  val sessionState = Map[String, Seq[ShoppingCartItem]]()
  
  def removeFromCart(sessionId: String, item: Device):Seq[ShoppingCartItem]  = {
    val updatedItems = sessionState.get(sessionId)
      .map(_.filterNot(_.item.id == item.id))
      .getOrElse(Seq())
    sessionState += (sessionId -> updatedItems)
    updatedItems
  }

  def upsertCart(sessionId: String, item: Device):Seq[ShoppingCartItem] = {
    val updatedItems = sessionState.get(sessionId) match {
      case Some(items) => {
        val updatedItem = items.find(_.item.id == item.id)
          .map(item => item.copy(count = (item.count + 1)))
          .getOrElse(ShoppingCartItem(item))
        updatedItem +: items.filterNot(_.item.id == item.id)
      }
      case None => Seq(ShoppingCartItem(item))
    }
    sessionState += (sessionId -> updatedItems)
    updatedItems

  }
}
