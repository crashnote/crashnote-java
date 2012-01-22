package com.crashnote.logger.test.unit.jul

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

import com.crashnote.test.defs.BaseMockSpec
import com.crashnote.core.model.types.LogLevel
import com.crashnote.jul.impl.JulEvt
import java.util.logging._

class JulEvtSpec
    extends BaseMockSpec[LogRecord] {

    "JUL Event" should {

        "instantiate" >> {
            val args = Array(new java.lang.Long(1), "test")
            val err = new RuntimeException("oops")

            val m_evt = mock[LogRecord]
            m_evt.getLevel returns Level.SEVERE
            m_evt.getThreadID returns 1
            m_evt.getMessage returns "oops"
            m_evt.getThrown returns err
            m_evt.getLoggerName returns "com.example"
            m_evt.getMillis returns 123456789L

            val r = new JulEvt(m_evt, null)
            r.getLoggerName === "com.example"
            r.getLevel === LogLevel.ERROR
            r.getThreadName === "1"
            r.getThrowable === err
            r.getMessage === "oops"
            r.getTimeStamp === 123456789L
        }
    }
}