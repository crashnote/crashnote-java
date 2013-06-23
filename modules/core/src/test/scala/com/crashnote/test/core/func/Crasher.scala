/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
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
package com.crashnote.test.core.func

import com.crashnote.core.config._

object Crasher {

  def main(args: Array[String]) {
    apply(new RuntimeException("test"), "localhost", 9000)
    Thread.sleep(1000)
    sys.exit()
  }

  def apply(error: Throwable,
            url: String = "send.crashnote.io",
            port: Int = 80) {

    // create config
    val confLoader = new ConfigLoader {
      override def fromSystemProps() =
        fromString {
          s"""
            |crashnote.key = "00000000-0000-0000-0000-000000000000"
            |crashnote.projectId = 42
            |crashnote.network.port = $port
            |crashnote.network.host = "$url"
            |crashnote.network.protocol = http
            |crashnote.debug = true
            |crashnote.enabled = true
          """.stripMargin
        }
    }
    val config = new CrashConfigFactory[CrashConfig](confLoader).get()

    // create reporter
    val reporter = config.getReporter
    reporter.start()

    // CRASH!
    reporter.uncaughtException(Thread.currentThread(), error)
  }
}