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
                1 === 1 // TODO
            }
            "fail when key missing" >> {
                1 === 1
            }
        }

        "act as factory" >> {
            "for logger" >> {
                c.getLogger("test") !== null
                c.getLogger(this.getClass) !== null
            }
            "for builder" >> {
                c.getBuilder !== null
            }
            "for sender" >> {
                c.getSender !== null
            }
            "for collector" >> {
                c.getCollector !== null
            }
            "for system util" >> {
                c.getSystemUtil !== null
            }
            "for reporter" >> {
                c.getReporter !== null
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