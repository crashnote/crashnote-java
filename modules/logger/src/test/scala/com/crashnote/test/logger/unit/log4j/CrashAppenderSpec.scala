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
package com.crashnote.test.logger.unit.log4j

import com.crashnote.test.logger.defs._
import com.crashnote.log4j.CrashAppender
import com.crashnote.log4j.impl.Log4jEvt
import org.apache.log4j.Level
import org.apache.log4j.spi._

class CrashAppenderSpec
    extends TargetMockSpec[CrashAppender] with AppenderEnv[CrashAppender, LoggingEvent] {

    "instantiate and append and close appender" >> {
        def example(descr: String, factory: () => CrashAppender) =
            "when created via " + descr >> new Mock() {

                // instantiate
                create(factory)

                target.isStarted === true
                there was one(m_reporter).start()

                // publish
                target.doAppend(m_evt)

                there was one(m_reporter).start() then
                    one(m_reporter).doAcceptLog("com.example") then
                    one(m_reporter).reportLog(any[Log4jEvt])

                // close
                target.close()

                target.isStarted === false
                there was one(m_reporter).stop()
            }

        val cases = Seq(
            ("factory", () => new CrashAppender(m_confFactory)),
            ("existing config", () => new CrashAppender(m_conf, m_reporter)))

        cases.foreach {
            case (descr, handler) => example(descr, handler)
        }
    }

    override def configure(config: C) = {
        m_evt = mock[LoggingEvent]
        m_evt.getThrowableInformation returns new ThrowableInformation(err)
        m_evt.getLevel returns Level.ERROR
        m_evt.getLoggerName returns "com.example"

        super.configure(config)
    }
}