package akka.testkit

import akka.actor.ActorSystem
import org.specs2.mutable.After
import com.typesafe.config.ConfigFactory
import java.io.File

object TestSupport {

  /** Simple specs2 bridge for Akka TestKit. */
  abstract class AkkaTestkitContext(actorSystem: ActorSystem) extends TestKit(actorSystem) with ImplicitSender with After {
    private[testkit] var owner = false

    def this() = {
      this(ActorSystem())
      owner = true
    }

    def after {
      if (owner) system.shutdown()
    }
  }

  /** Simple specs2 bridge for Akka TestKit. */
  abstract class AkkaPersistentTestkitContext(actorSystem: ActorSystem) extends AkkaTestkitContext(actorSystem) {

    def this() = {
      this(ActorSystem("TestActorSystem", ConfigFactory.parseString(
        """
        |akka.loglevel = "DEBUG"
        |   akka.persistence {
		|	journal.leveldb.dir = "target/example/journal"
		|	snapshot-store.local.dir = "target/example/snapshots"
		|
		|	# DO NOT USE THIS IN PRODUCTION !!!
		|	# See also https://github.com/typesafehub/activator/issues/287
		|	journal.leveldb.native = false
        | }
        |akka.actor.debug {
        |   receive = on
        |   autoreceive = on
        |   lifecycle = on
        |}
      """.stripMargin)))
      println("constructor ")
      owner = true
    }

    override def after {
      super.after
      if (owner) {
        deleteLocalJournalByConfigKey("akka.persistence.journal.leveldb.dir")
        deleteLocalJournalByConfigKey("akka.persistence.snapshot-store.local.dir")
      }
      def deleteLocalJournalByConfigKey(configKey: String) = {
        val dir = system.settings.config.getString(configKey)
        new File(dir).delete()
      }
    }
  }
}
