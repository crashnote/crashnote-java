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
package com.crashnote.core.test.unit.config

import com.crashnote.core.test.defs.MockSpec
import com.crashnote.core.config._
import com.crashnote.core.util.SystemUtil
import com.crashnote.core.log.LogLog
import ConfigFactory._
import com.crashnote.core.test.defs.stubs._
import java.util.Properties

class ConfigFactorySpec
    extends MockSpec[ConfigFactory[ConfigStub]] {

    var m_conf: C = _
    var m_sysUtil: SystemUtil = _
    var factory: ConfigFactory[C] = _

    "Config Factory" should {

        val props = toProps(Map(
            "host" -> "host.com", "crashnote.host" -> "crashnote.host",
            "port" -> "66", "crashnote-Port" -> "99",
            "sYnc" -> "true", "crashnote.SYNC" -> "false",
            "secure" -> "true", "crashnote_seCure" -> "false",
            "SSLPORT" -> "66", "crashnote-sslPort" -> "99",
            "version" -> "1.0", "crashnote.VERsion" -> "1.0.0"
        ))

        "create configuration instance" >> {
            "by reading from" >> {
                "file" >> {
                    "with default location" >> new Mocked() {
                        m_sysUtil.loadProperties(PROP_FILE) returns props
                        target.get()
                        expect {
                            one(m_conf).setHost("host.com")
                            one(m_conf).setPort("66")
                            one(m_conf).setSync("true")
                            one(m_conf).setSecure("true")
                            one(m_conf).setSSLPort("66")
                            one(m_conf).setVersion("1.0")
                        }
                    }
                    "with location specified by system" >> new Mocked() {
                        m_sysUtil.getProperty(PROP_FILE_CONF, PROP_FILE) returns "sys.properties"
                        m_sysUtil.loadProperties("sys.properties") returns props

                        target.get()
                        there was one(m_conf).setHost("host.com")
                    }
                    "with location specified by env" >> new Mocked() {
                        m_sysUtil.getEnv(PROP_FILE_CONF, PROP_FILE) returns "env.properties"
                        m_sysUtil.getProperty(PROP_FILE_CONF, "env.properties") returns "env.properties"
                        m_sysUtil.loadProperties("env.properties") returns props

                        target.get()
                        there was one(m_conf).setHost("host.com")
                    }
                    "with location specified by system AND env" >> new Mocked() {
                        m_sysUtil.getEnv(PROP_FILE_CONF, PROP_FILE) returns "env.properties"
                        m_sysUtil.getProperty(PROP_FILE_CONF, "env.properties") returns "sys.properties"
                        m_sysUtil.loadProperties("sys.properties") returns props

                        target.get()
                        there was one(m_conf).setHost("host.com")
                    }
                }
                "environment" >> new Mocked() {
                    m_sysUtil.getEnvProperties returns props
                    target.get()
                    expect {
                        one(m_conf).setHost("crashnote.host")
                        one(m_conf).setPort("99")
                        one(m_conf).setSync("false")
                        one(m_conf).setSecure("false")
                        one(m_conf).setSSLPort("99")
                        one(m_conf).setVersion("1.0.0")
                    }
                }
                "system" >> new Mocked() {
                    m_sysUtil.getProperties returns props
                    target.get()
                    expect {
                        one(m_conf).setHost("crashnote.host")
                        one(m_conf).setPort("99")
                        one(m_conf).setSync("false")
                        one(m_conf).setSecure("false")
                        one(m_conf).setSSLPort("99")
                        one(m_conf).setVersion("1.0.0")
                    }
                }
                "file first, then environment and system last" >> new Mocked() {
                    m_sysUtil.getProperties returns toProps(Map("crashnote.port" -> "1"))
                    m_sysUtil.getEnvProperties returns toProps(Map("crashnote.port" -> "2"))
                    m_sysUtil.loadProperties(PROP_FILE) returns toProps(Map("port" -> "3"))

                    target.get()

                    there was one(m_conf).setPort("3") then
                        one(m_conf).setPort("2") then
                        one(m_conf).setPort("1")
                }
            }
            "but throw exception for" >> {
                "missing key" >> new Mocked() {
                    m_conf.getKey returns null
                    target.get() must throwA[IllegalArgumentException]
                }
                "empty key" >> new Mocked() {
                    m_conf.getKey returns ""
                    target.get() must throwA[IllegalArgumentException]
                }
                "nonsense key" >> new Mocked() {
                    m_conf.getKey returns "nonsense"
                    target.get() must throwA[IllegalArgumentException]
                }
                "yet ignore invalid key when disabled" >> new Mocked() {
                    m_conf.isEnabled returns false
                    m_conf.getKey returns "invalid"
                    target.get() // must not throw exception
                    1 === 1
                }
            }
        }
    }

    def configure(config: C) = {
        m_conf = mock[C]
        m_conf.isEnabled returns true
        m_conf.getKey returns "00000000000000000000000000000000"
        m_conf.getLogger(anyClass) returns new LogLog("")

        new ConfigFactoryStub(m_conf)
    }

    override def mock() {
        m_sysUtil = _mock[SystemUtil]

        m_sysUtil.loadProperties(anyString) returns new Properties()
        m_sysUtil.getEnv(PROP_FILE_CONF, PROP_FILE) returns PROP_FILE
        m_sysUtil.getProperty(PROP_FILE_CONF, PROP_FILE) returns PROP_FILE
    }
}