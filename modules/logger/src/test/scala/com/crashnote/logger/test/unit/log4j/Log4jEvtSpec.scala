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
package com.crashnote.logger.test.unit.log4j

import scala.collection.JavaConversions._
import com.crashnote.test.defs.BaseMockSpec
import com.crashnote.log4j.impl.Log4jEvt
import com.crashnote.core.model.types.LogLevel
import org.apache.log4j._
import spi._

class Log4jEvtSpec
    extends BaseMockSpec[LoggingEvent] {

    "Log4j Event" should {

        "instantiate" >> {
            "example 1" >> {
                val m_evt = mock[LoggingEvent]
                m_evt.getLevel returns Level.FATAL
                m_evt.getThreadName returns "main"
                m_evt.getLoggerName returns "com.example"

                val r = new Log4jEvt(m_evt, null)
                r.getLoggerName === "com.example"
                r.getLevel === LogLevel.FATAL
                r.getThreadName === "main"
                r.getArgs === null
                r.getThrowable === null
                r.getMessage === null
                r.getTimeStamp === 0 // can't mock
            }
            "example 2" >> {
                val err = new RuntimeException("oops")
                val m_evt = mock[LoggingEvent]
                m_evt.getLevel returns Level.ERROR
                m_evt.getMessage returns "oops"
                m_evt.getThrowableInformation returns new ThrowableInformation(err)

                val r = new Log4jEvt(m_evt, Map("test" -> "data"))
                r.getLevel === LogLevel.ERROR
                r.getMessage === "oops"
                r.getThrowable === err

                r.defer()

                r.getMDC.get("test") === "data"
            }
        }
    }
}