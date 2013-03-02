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

import com.crashnote.core.config.{ConfigLoader, CrashConfigFactory, CrashConfig}
import com.crashnote.test.core.defs.TargetMockSpec

class ConfigFactorySpec
  extends TargetMockSpec[CrashConfigFactory[CrashConfig]] {

  "Config Factory" should {

    "create configuration instance" >> {

      "with correct override order" >> {
        "#1 system props" >> new Mock {
          val c = target.get()
          c.getKey === "11"
        }
        "#2 env props" >> new Mock {
          val c = target.get()
          c.getAppBuild === "22"
        }
        "#3 user props" >> new Mock {
          val c = target.get()
          c.getAppEnv === "33"
        }
        "#4 about props" >> new Mock {
          val c = target.get()
          c.getAppVersion === "44"
        }
        "#5 default props" >> new Mock {
          val c = target.get()
          c.getConnectionTimeout === 55
        }
      }

      "use cached version for further calls" >> new Mock {
        // #1
        target.get()

        reset(m_loader)

        // #2
        target.get()

        verifyUntouched(m_loader)
      }

      "print config in debug mode" >> new Mock(DEBUG) {
        // execute
        var (out, _) = capture {
          target.get()
        }

        // verify
        println(out)
        out must contain( """"crashnote""")
      }
    }
  }

  // SETUP =====================================================================================

  var m_loader: ConfigLoader = _

  override def mock() {
    m_loader.fromSystemProps() returns
      getConf(genProps(10) ::: List("debug" -> m_conf.isDebug.toString), "sys props")
    m_loader.fromEnvProps() returns
      getConf(genProps(20), "env props")
    m_loader.fromFile("crashnote") returns
      getConf(genProps(30), "user props")
    m_loader.fromFile("crashnote.about") returns
      getConf(genProps(40), "about props")
    m_loader.fromFile("crashnote.default") returns
      getConf(genProps(50), "default props")
  }

  def configure(config: C) = {
    m_loader = spy(new ConfigLoader)
    new CrashConfigFactory[CrashConfig](m_loader)
  }

  // HELPER =====================================================================================

  private def getConf(l: List[(String, Any)], descr: String) =
    (new ConfigLoader).fromProps(toConfProps(l), descr)

  private def genProps(base: Int) =
    List(
      "key" -> (base + 1),
      "app.build" -> (base + 2),
      "app.env" -> (base + 3),
      "app.version" -> (base + 4),
      "network.timeout" -> (base + 5)
    ).take(base / 10)
}