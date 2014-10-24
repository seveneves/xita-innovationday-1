package com.example.analytics

import akka.actor._
import akka.contrib.pattern.{ClusterSingletonProxy, ClusterSingletonManager}

/**
 * Convenience accessor to the cluster singleton ActorRef of the AnalyticsActor.
 */
object Analytics {
  def apply(system: ActorSystem): ActorRef = AnalyticsExtension(system.asInstanceOf[ExtendedActorSystem]).actorRef
}

/**
 * Simple extension that starts the AnalyticsActor as a cluster singleton and provides
 * access to the ActorRef of the local cluster singleton proxy.
 */
private[analytics] object AnalyticsExtension extends ExtensionKey[AnalyticsExtension]
private[analytics] class AnalyticsExtension(system: ExtendedActorSystem) extends Extension {
  private val singletonManager = system.actorOf(
    ClusterSingletonManager.props(
      singletonProps = AnalyticsActor.props,
      singletonName = AnalyticsActor.name,
      terminationMessage = PoisonPill,
      role = None
    ), name = "analytics-singleton"
  )

  val actorRef = system.actorOf(
    ClusterSingletonProxy.props(
      singletonPath = singletonManager.path.child(AnalyticsActor.name).toStringWithoutAddress,
      role = None
    ), name = "analytics-proxy"
  )
}