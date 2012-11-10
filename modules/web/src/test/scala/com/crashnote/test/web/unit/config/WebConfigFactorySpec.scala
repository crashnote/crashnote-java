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
package com.crashnote.test.web.unit.config

import com.crashnote.web.config.{WebConfig, WebConfigFactory}
import com.crashnote.test.base.defs.MockSpec
import com.crashnote.core.config.ConfigLoader

class WebConfigFactorySpec
    extends MockSpec {

    "Web Config Factory" should {

        "create configuration instance" >> {
            val factory = new WebConfigFactory[WebConfig]()
            val c = factory.create()
            c must haveClass[WebConfig]
        }

        "load 'crashnote.web.conf' before other default files but after user conf file" >> {
            // mock
            val l = new ConfigLoader
            val m_loader = spy(l)
            m_loader.fromFile("crashnote.web") returns
                l.fromProps(toConfProps(List("request.max-parameter-size" -> 1000)), "web props")
            m_loader.fromFile("crashnote.default") returns
                l.fromProps(toConfProps(List("request.max-parameter-size" -> 10000)), "default props")

            // execute
            var c = (new WebConfigFactory[WebConfig](m_loader)).create()

            // verify
            c.getMaxRequestParameterSize === 1000

            // ###

            // mock
            m_loader.fromFile("crashnote") returns
                l.fromProps(toConfProps(List("request.max-parameter-size" -> 100)), "user props")

            // execute
            c = (new WebConfigFactory[WebConfig](m_loader)).create()

            // verify
            c.getMaxRequestParameterSize === 100
        }
    }
}