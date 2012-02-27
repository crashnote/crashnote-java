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
import com.crashnote.core.log.LogLog
import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.servlet.config.ServletConfigFactory
import com.crashnote.core.util.SystemUtil
import com.crashnote.core.config.ConfigFactory._

class ServletConfigFactorySpec
    extends MockSpec[ServletConfigFactory[ConfigStub]] {

    var m_conf: ConfigStub = _
    var m_sysUtil: SystemUtil = _
    var m_filterConf: FilterConfig = _

    val props = toProps(Map(
        "skipHeaders" -> "true", "crashnote.skipHeaders" -> "false",
        "skipSession" -> "true", "crashnote-skipSession" -> "false",
        "maxRequestDataSize" -> "42", "crashnote_maxRequestDataSize" -> "69",
        "ignoreLocalRequests" -> "true", "crashnote.ignoreLocalRequests" -> "false",
        "requestParameterFilter" -> "abc,xyz", "crashnote_requestParameterFilter" -> "def,uvw",
        "port" -> "1"
    ))

    "Servlet Config Factory" should {

        "create configuration instance" >> {
            "by reading properties from" >> {
                "filter" >> new Mocked() {
                    target.get()

                    expect {
                        one(m_conf).setMaxRequestParameterSize("42")
                        one(m_conf).setIgnoreLocalRequests("true")
                        one(m_conf).setSkipHeaderData("true")
                        one(m_conf).setSkipSessionData("true")
                        one(m_conf).addRequestFilter("abc")
                        one(m_conf).addRequestFilter("xyz")
                    }
                }
                "system" >> new Mocked() {
                    m_sysUtil.getProperties returns props
                    target.get()

                    expect {
                        one(m_conf).setMaxRequestParameterSize("69")
                        one(m_conf).setIgnoreLocalRequests("false")
                        one(m_conf).setSkipHeaderData("false")
                        one(m_conf).setSkipSessionData("false")
                        one(m_conf).addRequestFilter("def")
                        one(m_conf).addRequestFilter("uvw")
                    }
                }
                "from filter first" >> new Mocked() {
                    m_sysUtil.loadProperties(PROP_FILE) returns toProps(Map("port" -> "2"))
                    target.get()

                    there was one(m_conf).setPort("1") then one(m_conf).setPort("2")
                }
            }
        }
    }

    def configure(config: C) = {
        m_conf = mock[C]
        m_conf.isEnabled returns true
        m_conf.getKey returns "00000000000000000000000000000000"
        m_conf.getLogger(anyClass) returns new LogLog("")

        m_filterConf = mock[FilterConfig]
        m_filterConf.getInitParameterNames.asInstanceOf[javaEnum[Object]] returns props.keys()
        m_filterConf.getInitParameter(anyString) answers (name => props.getProperty(name.toString))

        new ConfigFactoryStub(m_filterConf, m_conf)
    }

    override def mock() {
        m_sysUtil = _mock[SystemUtil]
    }
}
