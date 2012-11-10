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

class AppengineConfigSpec
    extends MockSpec with BeforeExample {

    "AppEngine Config" should {

        "inherit from Servlet Config" >> {
            c must haveSuperclass[ServletConfig]
        }

        //"always be in sync mode" >> {
        //            "by default" >> new Mock(SYNC) {
        //                target.isSync === true
        //            }
        //            "even when config is set to async" >> new Mock(ASYNC) {
        //                target.isSync === true
        //            }
        //        }
    }

    // SETUP ======================================================================================

    var c: AppengineConfig = _

    def before {
        val m_filterConf = mock[FilterConfig]

        val l = new ConfigLoader
        val m_loader = spy(l)
        m_loader.fromSystemProps() returns
            l.fromProps(toConfProps(List("key" -> "0000000-00000-0000-0000-000000000000")), "sys props")

        c = (new AppengineConfigFactory(m_filterConf, m_loader)).get
    }
}