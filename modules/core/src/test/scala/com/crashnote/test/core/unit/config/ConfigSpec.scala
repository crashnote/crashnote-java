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

import java.util.Date
import org.specs2.specification.BeforeExample
import com.crashnote.test.base.defs.MockSpec
import com.crashnote.core.config.{ConfigLoader, CrashConfigFactory, CrashConfig}
import com.crashnote.core.log.LogLog
import com.crashnote.core.build.Builder
import com.crashnote.core.send.Sender
import com.crashnote.core.collect.Collector
import com.crashnote.core.util.SystemUtil
import com.crashnote.core.report.Reporter
import com.crashnote.core.model.excp.CrashnoteException
import com.crashnote.external.config.ConfigFactory

class ConfigSpec
  extends MockSpec with BeforeExample {

  "Config" should {

    "return" >> {
      "POST URL" >> {
        val bm = List("key" -> "abc", "projectId" -> "xyz", "network.host" -> "mycompany.com")

        "with SSL" >> {
          c = getConfig(bm ::: List("network.port-ssl" -> 666, "network.ssl" -> true))
          c.getPostURL === "https://mycompany.com:666/?key=abc&projectId=xyz"
        }
        "without SSL" >> {
          c = getConfig(bm ::: List("network.port" -> 8080, "network.ssl" -> false))
          c.getPostURL === "http://mycompany.com:8080/?key=abc&projectId=xyz"
        }
      }
      "start time" >> {
        c.getStartTime must beLessThan(new Date().getTime)
      }
    }

    "validate configuration" >> {
      "skip when disabled" >> {
        val (out, _) = capture {
          c.validate(null)
        }
        out must contain("OFF")
      }
      "fail when project id missing" >> {
        getConfig(List("enabled" -> true, "key" -> "0000000-00000-0000-0000-000000000000")).
          validate(null) must throwA[IllegalStateException]
      }
      "fail when key missing" >> {
        getConfig(List("enabled" -> true, "key" -> "", "projectId" -> "xyz")).
          validate(null) must throwA[IllegalStateException]
      }
      "fail when key invalid" >> {
        getConfig(List("enabled" -> true, "key" -> "abra cadabra", "projectId" -> "xyz")).
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

    "read config key" >> {
      "of type String" >> {
        "successfully" >> {
          getConfig() must not beNull
        }
        "but throw exception when missing" >> {
          getConfigWith().getKey must throwA[CrashnoteException]
        }
        /*
        "but throw exception when wrong type" >> {
            getConfigWith(("key" -> "[42]")).getKey must throwA[CrashnoteException]
        }
        */
      }
      "of type Bool" >> {
        "successfully" >> {
          getConfig().isSync must beFalse
        }
        "but throw exception when missing" >> {
          getConfigWith().isSync must throwA[CrashnoteException]
        }
        "but throw exception when wrong type" >> {
          getConfigWith(("sync" -> "$$")).isSync must throwA[CrashnoteException]
        }
      }
      "of type Millis" >> {
        "successfully" >> {
          getConfig().getConnectionTimeout must beGreaterThan(1000)
        }
        "but throw exception when missing" >> {
          getConfigWith().getConnectionTimeout must throwA[CrashnoteException]
        }
        "but throw exception when wrong format" >> {
          getConfigWith(("network.timeout" -> "true")).getConnectionTimeout must throwA[CrashnoteException]
        }
      }
      "of type List" >> {
        "successfully" >> {
          getConfig().getEnvironmentFilters must not be empty
        }
        "but throw exception when missing" >> {
          getConfigWith().getEnvironmentFilters must throwA[CrashnoteException]
        }
        "but throw exception when wrong format" >> {
          getConfigWith(("filter.environment" -> "42")).getEnvironmentFilters must throwA[CrashnoteException]
        }
      }
    }
  }

  // SETUP =====================================================================================

  var c: CrashConfig = _

  def before {
    c = getConfig()
  }

  def getConfig(m: List[(String, Any)] = List()) =
    getConf(m, spy(_))

  def getConfigWith(m: (String, Any)*) =
    getConf(m.toList, cf => {
      val m = mock[ConfigLoader]
      m.fromFile(anyString) returns ConfigFactory.empty()
      m.fromEnvProps() returns ConfigFactory.empty()
      m
    })

  private def getConf(m: List[(String, Any)], fn: (ConfigLoader) => ConfigLoader) = {
    val cl = new ConfigLoader
    val _cl = fn(cl)
    _cl.fromSystemProps() returns cl.fromProps(toConfProps(m), "spec")
    (new CrashConfigFactory[CrashConfig](_cl)).get
  }
}