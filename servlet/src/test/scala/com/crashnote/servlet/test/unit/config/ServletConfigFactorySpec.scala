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
package com.crashnote.servlet.test.unit.config

import javax.servlet.FilterConfig

import com.crashnote.servlet.test.defs.stubs.ConfigStub
import com.crashnote.servlet.test.defs.stubs.ConfigFactoryStub
import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.servlet.config.ServletConfigFactory

class ServletConfigFactorySpec
    extends MockSpec[ServletConfigFactory[ConfigStub]] {

    setSequential()

    "Servlet Config Factory" should {

        "create configuration instance" >> {

            "by reading properties from filter" >> new Mocked()  {
                val c = target.get()

                c.getIgnoreLocalRequests === false
                c.getSkipHeaderData === false
                c.getSkipSessionData === true
            }

            "and override by system" >> new Mocked() {
                val c = target.get()

                c.getMaxRequestParameterSize === 69
            }
        }
    }

    var m_filterConf: FilterConfig = _

    val filterProps =
        toProps(Map(
            "request.skipHeaders" -> "false",
            "request.skip-session" -> "true",
            "request.ignore-localhost" -> "false"
        ))

    def configure(config: C) = {
        System.setProperty("crashnote.request.max-parameter-size", "69")

        m_filterConf = mock[FilterConfig]
        m_filterConf.getInitParameterNames.asInstanceOf[javaEnum[Object]] returns filterProps.keys()
        m_filterConf.getInitParameter(anyString) answers (name => filterProps.getProperty(name.toString))

        new ConfigFactoryStub(m_filterConf)
    }
}
