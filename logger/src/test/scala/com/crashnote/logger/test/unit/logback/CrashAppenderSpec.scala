package com.crashnote.logger.test.unit.logback

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

import com.crashnote.logger.test.defs._
import com.crashnote.logback.CrashAppender
import com.crashnote.logback.impl.LogbackEvt
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi._

class CrashAppenderSpec
    extends MockSpec[CrashAppender] with AppenderEnv[CrashAppender, ILoggingEvent] {

    "instantiate, append and close appender" >> {
        def example(descr: String, factory: () => CrashAppender) =
            "when created via " + descr >> new Mocked() {

                // instantiate
                create(factory)
                target.start()

                target.isStarted === true
                there was one(m_reporter).start()

                //publish
                target.doAppend(m_evt)

                there was one(m_reporter).start() then
                    one(m_reporter).doAcceptLog("com.example") then
                    one(m_reporter).reportLog(any[LogbackEvt])

                // close
                target.stop()

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
        m_evt = mock[ILoggingEvent]
        val m_thproxy = mock[ThrowableProxy]
        m_evt.getThrowableProxy returns m_thproxy
        m_thproxy.getThrowable returns err
        m_evt.getLevel returns Level.ERROR
        m_evt.getLoggerName returns "com.example"

        super.configure(config)
    }
}