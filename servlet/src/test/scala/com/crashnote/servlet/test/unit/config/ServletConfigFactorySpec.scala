package com.crashnote.servlet.test.unit.config

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
        "skipRequestHeader" -> "true", "crashnote.skipRequestHeader" -> "false",
        "skipRequestSession" -> "true", "crashnote-skipRequestSession" -> "false",
        "requestFilter" -> "abc,xyz", "crashnote_requestFilter" -> "def,uvw",
        "port" -> "1"
    ))

    "Servlet Config Factory" should {

        "create configuration instance" >> {
            "by reading from" >> {
                "filter" >> new Mocked() {
                    target.get()

                    expect {
                        one(m_conf).setSkipHeaderData("true")
                        one(m_conf).setSkipSessionData("true")
                        one(m_conf).addRequestFilters("abc")
                        one(m_conf).addRequestFilters("xyz")
                    }
                }
                "system" >> new Mocked() {
                    m_sysUtil.getProperties returns props
                    target.get()

                    expect {
                        one(m_conf).setSkipHeaderData("false")
                        one(m_conf).setSkipSessionData("false")
                        one(m_conf).addRequestFilters("def")
                        one(m_conf).addRequestFilters("uvw")
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
