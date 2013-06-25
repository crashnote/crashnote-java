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

import javax.servlet.FilterConfig

import com.crashnote.appengine.config.{AppengineConfig, AppengineConfigFactory}
import com.crashnote.core.config.ConfigLoader
import com.crashnote.servlet.config.ServletConfigFactory
import com.crashnote.test.appengine.defs.TargetMockSpec
import java.util.Properties

class AppengineConfigFactorySpec
    extends TargetMockSpec[AppengineConfigFactory] {

    "Appengine Config Factory" should {

        "inherit from Servlet Config Factory" >> new Configured {
            getFactory() must haveSuperclass[ServletConfigFactory[_]]
        }

        "create configuration instance" >> new Configured {
            target.get must haveClass[AppengineConfig]
        }

        "load dynamic config before default file confs" >> new Configured {

            // mock
            m_loader.fromFile("crashnote.default") returns
                loader.fromProps(toConfProps(List("enabled" -> true, "ignore-localhost" -> true)), "default props")

            // execute
            var c = getFactory(defaultWebProps).get

            // verify
            c.isEnabled === false
            c.getIgnoreLocalRequests === false
        }
    }

    // SETUP ======================================================================================

    var loader: ConfigLoader = _
    var m_loader: ConfigLoader = _
    var m_filterConf: FilterConfig = _

    def configure(config: C) =
        getFactory(defaultWebProps)

    def getFactory(p: Properties = new Properties(), l: ConfigLoader = new ConfigLoader) = {
        loader = l
        m_loader = spy(l)
        m_filterConf = filterConfDefaultMock(p)

        if(p.isEmpty)
            new AppengineConfigFactory(m_filterConf)
        else
            new AppengineConfigFactory(m_filterConf, m_loader)
    }
}
