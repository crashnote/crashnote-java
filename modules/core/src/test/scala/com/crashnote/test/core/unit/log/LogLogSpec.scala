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
package com.crashnote.test.core.unit.log

import com.crashnote.core.log.{LogLog, LogLogFactory}
import com.crashnote.test.core.defs.TargetMockSpec
import com.crashnote.core.config.CrashConfig
import java.io._

class LogLogSpec
    extends TargetMockSpec[LogLogFactory] {

    "LogLog" should {

        "instantiate via factory" >> {

            "in production mode" >> {
                def result = {
                    loggr: LogLog =>
                        loggr.getName === prefix
                        loggr.isDebug === false
                }

                "default" >> new Configured() {
                    result(target.getLogger)
                }
                "via name" >> new Configured() {
                    result(target.getLogger("logger"))
                }
                "via class" >> new Configured() {
                    result(target.getLogger(this.getClass))
                }
            }

            "in debug mode" >> {
                def result = {
                    loggr: LogLog =>
                        loggr.getName === prefix
                        loggr.isDebug === true
                }

                "via name" >> new Configured(DEBUG) {
                    result(target.getLogger)
                }
                "via name" >> new Configured(DEBUG) {
                    result(target.getLogger("logger"))
                }
                "via name" >> new Configured(DEBUG) {
                    result(target.getLogger(this.getClass))
                }
            }
        }

        "print" >> {

            "in production mode" >> {
                "error" >> {
                    "with only message" >> new Configured() {
                        val loggr = target.getLogger
                        val (out, err) = capture { loggr.error("test") }
                        err must startWith(prefix)
                        err must contain("ERROR")
                        err must contain("test")
                        out must beEmpty
                    }
                    "with throwable" >> new Configured() {
                        val loggr = target.getLogger
                        val (out, err) = capture { loggr.error("test", th) }
                        err must endWith("oops")
                    }
                }
                "info" >> new Configured() {
                    val loggr = target.getLogger
                    val (out, err) = capture { loggr.info("test") }
                    out must startWith(prefix)
                    out must contain("INFO")
                    out must contain("test")
                    err must beEmpty
                }
                "warn" >> new Configured() {
                    val loggr = target.getLogger
                    val (out, err) = capture { loggr.warn("test") }
                    err must startWith(prefix)
                    err must contain("WARN")
                    err must contain("test")
                    out must beEmpty
                }
                "but no debug" >> new Configured() {
                    val loggr = target.getLogger
                    val (out, err) = capture { loggr.debug("test") }
                    out must beEmpty
                    err must beEmpty
                }
            }

            "in debug mode" >> {
                "debug" >> new Configured(DEBUG) {
                    "without arguments" >> {
                        val loggr = target.getLogger

                        val (out, err) = capture { loggr.debug("test") }
                        out must startWith(prefix)
                        out must contain("DEBUG")
                        out must contain("test")
                        err must beEmpty
                    }
                    "with arguments" >> {
                        val loggr = target.getLogger
                        val (out, err) = capture { loggr.debug("test: {}", "a") }
                        out must startWith(prefix)
                        out must contain("test")
                        out must endWith("a")
                        err must beEmpty
                    }
                }
            }
        }
    }

    // SETUP ======================================================================================

    def configure(config: CrashConfig) =
        new LogLogFactory(config)

    val prefix = "CRASHNOTE"
    val th = new RuntimeException("oops")
}