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
package com.crashnote.test.logger.unit.jul

import com.crashnote.core.model.types.LogLevel
import com.crashnote.jul.impl.JulEvt
import java.util.logging._
import com.crashnote.test.base.defs.BaseMockSpec

class JulEvtSpec
    extends BaseMockSpec[LogRecord] {

    "JUL Event" should {

        "instantiate" >> {
            val args = Array("Bob")
            val err = new RuntimeException("oops")

            // mock
            val m_evt = getMock(Level.SEVERE)

            m_evt.getThreadID returns 1
            m_evt.getMessage returns "oops"
            m_evt.getThrown returns err
            m_evt.getLoggerName returns "com.example"
            m_evt.getMillis returns 123456789L
            m_evt.getParameters returns args.asInstanceOf[Array[AnyRef]]

            // execute
            val r = new JulEvt(m_evt, null)

            // verify
            r.getLoggerName === "com.example"
            r.getLevel === LogLevel.ERROR
            r.getThreadName === "1"
            r.getArgs === Array("Bob")
            r.getThrowable === err
            r.getMessage === "oops"
            r.getTimeStamp === 123456789L
        }

        "convert log level" >> {
            "error" >> {
                new JulEvt(getMock(Level.SEVERE)).getLevel === LogLevel.ERROR
            }
            "warn" >> {
                new JulEvt(getMock(Level.WARNING)).getLevel === LogLevel.WARN
            }
            "info" >> {
                new JulEvt(getMock(Level.CONFIG)).getLevel === LogLevel.INFO
            }
            "debug" >> {
                new JulEvt(getMock(Level.FINE)).getLevel === LogLevel.DEBUG
                new JulEvt(getMock(Level.FINER)).getLevel === LogLevel.DEBUG
                new JulEvt(getMock(Level.FINEST)).getLevel === LogLevel.DEBUG
            }
        }
    }

    // SETUP ======================================================================================

    def getMock(l: Level) = {
        val m_evt = mock[LogRecord]
        m_evt.getLevel returns l
        m_evt
    }
}