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
package com.crashnote.test.servlet.unit.config

import java.util.Properties
import javax.servlet.FilterConfig

import com.crashnote.test.servlet.defs.TargetMockSpec
import com.crashnote.core.config.ConfigLoader
import com.crashnote.web.config.WebConfigFactory
import com.crashnote.servlet.config.{ServletConfig, ServletConfigFactory}

class ServletConfigFactorySpec
    extends TargetMockSpec[ServletConfigFactory[ServletConfig]] {

    "Servlet Config Factory" should {

        "inherit from Web Config Factory" >> new Configured {
            target must haveSuperclass[WebConfigFactory[_]]
        }

        "create configuration instance" >> new Configured {
            target.get must haveClass[ServletConfig]
        }

        "load servlet filter configs before user conf file" >> new Configured {

            // mock
            m_loader.fromFile("crashnote") returns
                loader.fromProps(toConfProps(List("request.max-parameter-size" -> 100)), "user props")

            // execute
            var c = getFactory(defaultWebProps).get

            // verify
            c.getMaxRequestParameterSize === 1000
        }
    }

    // SETUP ======================================================================================

    var loader: ConfigLoader = _
    var m_loader: ConfigLoader = _
    var m_filterConf: FilterConfig = _

    def configure(config: C) =
        getFactory(defaultWebProps)

    def getFactory(p: Properties, l: ConfigLoader = new ConfigLoader) = {
        loader = l
        m_loader = spy(l)
        m_filterConf = filterConfDefaultMock(p)

        new ServletConfigFactory[ServletConfig](m_filterConf, m_loader)
    }
}
