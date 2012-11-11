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

                "default" >> new Configured {
                    result(target.getLogger)
                }
                "via name" >> new Configured {
                    result(target.getLogger("logger"))
                }
                "via class" >> new Configured {
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
            def checkOutput(typeOf: String, content: String) = {
                txt: String =>
                    txt must startWith(prefix)
                    txt must contain(typeOf)
                    txt must contain(content)
            }

            "in production mode" >> {
                "error" >> {
                    "with message" >> new Configured {
                        val (out, err) = capture { target.getLogger.error("test") }
                        checkOutput("ERROR", "test")(err)
                        out must beEmpty
                    }
                    "with throwable" >> new Configured {
                        val (out, err) = capture { target.getLogger.error("test", th) }
                        checkOutput("ERROR", "oops")(err)
                        out must beEmpty
                    }
                }
                "info" >> new Configured {
                    val (out, err) = capture { target.getLogger.info("test") }
                    checkOutput("INFO", "test")(out)
                    err must beEmpty
                }
                "warn" >> {
                    "with message" >> new Configured {
                        val (out, err) = capture { target.getLogger.warn("test: {}", "A") }
                        checkOutput("WARN", "test: A")(err)
                        out must beEmpty
                    }
                    "with exception" >> new Configured {
                        val (out, err) = capture { target.getLogger.warn("test", th) }
                        checkOutput("WARN", "test")(err)
                        out must beEmpty
                    }
                    "with exception and parameters" >> new Configured {
                        val (out, err) = capture { target.getLogger.warn("test: {}", th, "A") }
                        checkOutput("WARN", "test: A")(err)
                        out must beEmpty
                    }
                }
                "but no debug" >> new Configured {
                    val (out, err) = capture { target.getLogger.debug("test") }
                    out must beEmpty
                    err must beEmpty
                }
            }

            "in debug mode" >> {
                "debug" >>  {
                    "without arguments" >> new Configured(DEBUG) {
                        val (out, err) = capture { target.getLogger.debug("test") }
                        checkOutput("DEBUG", "test")(out)
                        err must beEmpty
                    }
                    "with arguments" >> new Configured(DEBUG) {
                        val (out, err) = capture { target.getLogger.debug("test: {}", "A") }
                        checkOutput("DEBUG", "test: A")(out)
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