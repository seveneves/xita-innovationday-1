package com.example
import akka.actor._
import akka.contrib.pattern.ClusterSharding
import com.example.util.ActorRefFactoryProvider

trait CartManagerActorProvider {
  def cartHandler: ActorRef
}

trait ClusterCartManagerActorProvider extends CartManagerActorProvider with ActorRefFactoryProvider {

  lazy val cartHandler = actorRefFactory.actorOf(Props[CartManagerActor], "cart-handler")
}

object ClusterCartManagerActorProvider {
  def startSharding(system: ActorSystem) = {
    ClusterSharding(system).start(
      typeName = PersistentCartActor.shardName,
      entryProps = Some(PersistentCartActor.props(ProductRepo())),
      idExtractor = PersistentCartActor.idExtractor,
      shardResolver = PersistentCartActor.shardResolver)
  }
}