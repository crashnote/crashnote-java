/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crashnote.test.appengine.unit.config

import org.specs2.specification.BeforeExample
import javax.servlet.FilterConfig

import com.crashnote.test.base.defs.MockSpec
import com.crashnote.appengine.config.{AppengineConfig, AppengineConfigFactory}
import com.crashnote.core.config.ConfigLoader
import com.crashnote.servlet.config.ServletConfig
import com.crashnote.appengine.send.AppengineSender
import com.crashnote.appengine.util.AppengineUtil
import com.crashnote.appengine.collect.AppengineCollector

class AppengineConfigSpec
    extends MockSpec with BeforeExample {

    "AppEngine Config" should {

        "inherit from Servlet Config" >> {
            c must haveSuperclass[ServletConfig]
        }

        "act as factory" >> {
            "for sender" >> {
                c.getSender must haveClass[AppengineSender]
            }
            "for collector" >> {
                c.getCollector must haveClass[AppengineCollector]
            }
            "for system util" >> {
                c.getSystemUtil must haveClass[AppengineUtil]
            }
        }

        "always be in sync mode" >> {
            c = getConfig(("sync" -> true))
            c.isSync === true

            c = getConfig(("sync" -> false))
            c.isSync === true
        }
    }

    // SETUP ======================================================================================

    var c: AppengineConfig = _

    def before {
        c = getConfig()
    }

    def getConfig(p: (String, Any)*) = {
        val m_filterConf = mock[FilterConfig]

        val l = new ConfigLoader
        val m_loader = spy(l)
        m_loader.fromSystemProps() returns
            l.fromProps(toConfProps(p.toList ::: List("key" -> "0000000-00000-0000-0000-000000000000")), "sys props")

        (new AppengineConfigFactory(m_filterConf, m_loader)).get
    }
}