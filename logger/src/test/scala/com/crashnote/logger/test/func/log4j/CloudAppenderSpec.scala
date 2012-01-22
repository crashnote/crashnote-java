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
/*
package com.crashnote.specs.unit.log4j

import com.crashnote.log4j._
import com.crashnote.core.config.CoreConfig
import com.crashnote.defs.BaseMockSpec
import util.LogManager
import org.apache.log4j._

class CloudAppenderSpec extends BaseMockSpec[CloudAppender] {

    "Log4j CloudAppender" should {

        setSequential()

        "be instantiable" >> {
            target = new CloudAppender()

            "not require a layout" >> {
                target.requiresLayout() === false
            }
            "only accept logs of level ERROR" >> {
                target.getThreshold === Level.ERROR
            }
        }

        "interact with manager" >> {
            val m_mgr = _mock[LogManager]
            val m_conf = _mock[CoreConfig]

            /*"append" >> {
                 TODO
                val m_evt = mock[LoggingEvent]
                "execute when enabled" >> {
                    m_conf.isEnabled returns true
                    target.doAppend(evt)
                    there was one(m_mgr).append(evt)
                }
                "skip when disabled" >> {
                    m_conf.isEnabled returns false
                    target.doAppend(evt)
                    there was no(m_mgr)
                }
                "skip when low level" >> {
                    m_evt.getLevel returns Level.DEBUG
                    target.doAppend(m_evt)
                    there was no(m_mgr)
                    there was no(m_conf)
                }
            }
            */
            "close" >> {
                target.close()
                there was one(m_mgr).close()
            }
        }

        "be configurable" >> {
            val m_conf = _mock[CoreConfig]

            "async" >> {
                target.setAsync("false")
                there was one(m_conf).setAsync("false")
            }
            "port" >> {
                target.setPort("8080")
                there was one(m_conf).setPort("8080")
            }
            "host" >> {
                target.setHost("www.google.com")
                there was one(m_conf).setHost("www.google.com")
            }
            "key" >> {
                target.setKey("abc")
                there was one(m_conf).setKey("abc")
            }
            "sslPort" >> {
                target.setSslPort("444")
                there was one(m_conf).setSslPort("444")
            }
            "secure" >> {
                target.setSecure("y")
                there was one(m_conf).setSecure("y")
            }
            "enabled" >> {
                target.setEnabled("n")
                there was one(m_conf).setEnabled("n")
            }
        }
    }
}*/
