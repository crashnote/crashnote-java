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

import java.util.logging._
import com.crashnote.jul.CrashHandler
import com.crashnote.jul.impl.JulEvt
import com.crashnote.test.logger.defs._

class CrashHandlerSpec
    extends TargetMockSpec[CrashHandler] with AppenderEnv[CrashHandler, LogRecord] {

    "instantiate and publish and close handler" >> {
        def example(descr: String, factory: () => CrashHandler) =
            "when created via " + descr >> new Mock() {

                // instantiate
                create(factory)

                target.isStarted === true
                there was one(m_reporter).start()

                //publish
                target.publish(m_evt)

                there was one(m_reporter).doAcceptLog("com.example") andThen
                    one(m_reporter).reportLog(any[JulEvt])

                // close
                target.close()

                target.isStarted === false
                there was one(m_reporter).stop()
            }

        val cases = Seq(
            ("factory", () => new CrashHandler(m_confFactory)),
            ("existing config", () => new CrashHandler(m_conf, m_reporter)))

        cases.foreach {
            case (descr, handler) => example(descr, handler)
        }
    }

    // SETUP =====================================================================================

    override def configure(config: C) = {
        m_evt = mock[LogRecord]
        m_evt.getThrown returns err
        m_evt.getLevel returns Level.SEVERE
        m_evt.getLoggerName returns "com.example"

        super.configure(config)
    }
}