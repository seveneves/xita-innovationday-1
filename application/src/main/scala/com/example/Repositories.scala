package com.example

import akka.actor.{ExtendedActorSystem, Extension, ExtensionKey}
import com.example.CartMessages._
import com.example.ProductDomain._
import spray.json.JsonParser

import scala.io.Source

/**
 * Product repository trait
 */
trait ProductRepo extends Serializable {
  val products: Seq[Device]
  lazy val productMap: Map[String, Device] = products.map(p => p.id -> p).toMap
}

class ProductRepoImpl(val products: Seq[Device]) extends ProductRepo 


private[example] object ProductRepoExtension extends ExtensionKey[ProductRepoExtension]

private[example] class ProductRepoExtension(system: ExtendedActorSystem) extends Extension {
  val productRepo = {
    val products = productFilePaths.map { path =>
      val productStr = Source.fromInputStream(getClass.getResourceAsStream(path)).mkString
      val jsonAst = JsonParser(productStr)
      jsonAst.convertTo[Device]
    }
    new ProductRepoImpl(products)
  }

  /**
   * Creates a list of paths pointing to each product.json
   * E.g.: /root/phones/dell-streak-7.json, /root/phones/dell-venue.json etc.
   *
   * Necessary to load products this way in order to make it work
   * in test reading from classpath and reading inside jar
   */
  private def productFilePaths: Seq[String] = {
    val productDirRoot = "/root/phones"
    import org.json4s._
    import org.json4s.native.JsonMethods._
    val productsStr = Source.fromInputStream(ProductRepoExtension.getClass.getResourceAsStream(s"$productDirRoot/phones.json")).mkString
    val jsonAst = parse(productsStr)
    val productsJStr = jsonAst \\ "id" \\ classOf[JString]
    productsJStr.map(p => s"$productDirRoot/$p.json")
  }
}

/**
 * Session repository trait
 */
trait SessionRepo {
  import scala.collection._

  val sessionState = mutable.Map[String, mutable.Seq[ShoppingCartItem]]()

  def removeFromCart(sessionId: String, item: Device): mutable.Seq[ShoppingCartItem] = {
    val updatedItems = sessionState.get(sessionId)
      .map(_.filterNot(_.item.id == item.id))
      .getOrElse(mutable.Seq())
    sessionState += (sessionId -> updatedItems)
    updatedItems
  }

  def getCartItems(sessionId: String) = sessionState.getOrElse(sessionId, mutable.Seq())

  def checkoutCart(sessionId: String): mutable.Seq[ShoppingCartItem] = {
    val items = getCartItems(sessionId)
    sessionState += (sessionId -> mutable.Seq())
    items
  }

  def upsertCart(sessionId: String, item: Device): mutable.Seq[ShoppingCartItem] = {
    val updatedItems = sessionState.get(sessionId) match {
      case Some(items) => {
        val updatedItem = items.find(_.item.id == item.id)
          .map(item => item.copy(count = item.count + 1))
          .getOrElse(ShoppingCartItem(item))
        updatedItem +: items.filterNot(_.item.id == item.id)
      }
      case None => mutable.Seq(ShoppingCartItem(item))
    }
    sessionState += (sessionId -> updatedItems)
    updatedItems

  }
}
object SessionRepo extends SessionRepo

