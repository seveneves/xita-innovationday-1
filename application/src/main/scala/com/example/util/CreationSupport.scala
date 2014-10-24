package com.example.util
import akka.actor.{ ActorContext, ActorRef, Props }
import akka.actor.ActorRefFactory

trait CreationSupport {
  def getChild(name: String): Option[ActorRef]
  def createChild(props: Props, name: String): ActorRef
  def getOrCreateChild(props: Props, name: String): ActorRef = getChild(name).getOrElse(createChild(props, name))
}

trait ActorContextCreationSupport extends CreationSupport {
  def context: ActorContext

  def getChild(name: String): Option[ActorRef] = context.child(name)
  def createChild(props: Props, name: String): ActorRef = context.actorOf(props, name)
}

trait ActorRefFactoryProvider{
   /**
   * An ActorRefFactory needs to be supplied by the class mixing us in
   * (mostly either the service actor or the service test)
   */
  implicit def actorRefFactory: ActorRefFactory

}