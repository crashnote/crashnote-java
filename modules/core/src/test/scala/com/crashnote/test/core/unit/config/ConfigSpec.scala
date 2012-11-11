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
package com.crashnote.test.core.unit.config

import com.crashnote.test.base.defs.MockSpec
import org.specs2.specification.BeforeExample
import com.crashnote.core.config.{ConfigLoader, CrashConfigFactory, CrashConfig}
import java.util.Date
import com.crashnote.external.config.Config
import com.crashnote.core.log.LogLog
import com.crashnote.core.build.Builder
import com.crashnote.core.send.Sender
import com.crashnote.core.collect.Collector
import com.crashnote.core.util.SystemUtil
import com.crashnote.core.report.Reporter

class ConfigSpec
    extends MockSpec with BeforeExample {

    "Config" should {

        "return" >> {
            "POST URL" >> {
                val bm = List("key" -> 42, "network.host" -> "mycompany.com")

                "with SSL" >> {
                    c = getConfig(bm ::: List("network.port-ssl" -> 666, "network.ssl" -> true))
                    c.getPostUrl === "https://mycompany.com:666/api/errors?key=42"
                }
                "without SSL" >> {
                    c = getConfig(bm ::: List("network.port" -> 8080, "network.ssl" -> false))
                    c.getPostUrl === "http://mycompany.com:8080/api/errors?key=42"
                }
            }
            "start time" >> {
                c.getStartTime must beLessThan(new Date().getTime)
            }
        }

        "validate configuration" >> {
            "skip when disabled" >> {
                val (out, _) = capture { c.validate(null) }
                out must contain ("OFF")
            }
            "fail when key missing" >> {
                getConfig(List("enabled" -> true, "key" -> "")).
                    validate(null) must throwA[IllegalStateException]
            }
        }

        "act as factory" >> {
            "for logger" >> {
                c.getLogger("test") must haveClass[LogLog]
                c.getLogger(this.getClass) must haveClass[LogLog]
            }
            "for builder" >> {
                c.getBuilder must haveClass[Builder]
            }
            "for sender" >> {
                c.getSender must haveClass[Sender]
            }
            "for collector" >> {
                c.getCollector must haveClass[Collector]
            }
            "for system util" >> {
                c.getSystemUtil must haveClass[SystemUtil]
            }
            "for reporter" >> {
                c.getReporter must haveClass[Reporter]
            }
        }
    }

    // SETUP =====================================================================================

    var c: CrashConfig = _

    def before {
        c = getConfig()
    }

    def getConfig(m: List[(String, Any)] = List()) = {
        val cf = new ConfigLoader
        val _cf = spy(cf)
        _cf.fromSystemProps() returns cf.fromProps(toConfProps(m), "spec")
        val c = (new CrashConfigFactory[CrashConfig](_cf)).get
        c
    }
}