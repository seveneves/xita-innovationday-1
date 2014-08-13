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

  lazy val productMap: Map[String, Device] = products.map(p => p.name -> p).toMap
}

trait SessionRepo {
  import collection.mutable._
  val sessionState = Map[String, ShoppingCartItem]()
}
