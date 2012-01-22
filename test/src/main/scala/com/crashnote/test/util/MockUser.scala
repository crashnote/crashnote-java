package com.crashnote.test.util

import akka.dispatch.Dispatchers
import akka.actor.Actor
import akka.config.Supervision.Permanent
import java.net._

class MockUser extends Actor {

    self.lifeCycle = Permanent
    self.dispatcher = MockUser.dispatcher

    def receive = {
        case path: String => visitPath(path)
        case _ =>
    }

    private def visitPath(url: String) {
        val conn = (new URL(url).openConnection).asInstanceOf[HttpURLConnection]
        try conn.getResponseCode catch {
            case e => println(e)
        }
        finally {
            conn.disconnect()
        }
    }
}

object MockUser {

    val dispatcher =
        Dispatchers.newExecutorBasedEventDrivenWorkStealingDispatcher("pool")
            .setCorePoolSize(100).setMaxPoolSize(100).build
}