/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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