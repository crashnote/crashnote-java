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
package com.crashnote.servlet.test.unit.report

import javax.servlet.http.HttpServletRequest
import com.crashnote.core.report.impl.processor.Processor
import com.crashnote.core.model.log.ILogSession
import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.servlet.test.defs.stubs.ConfigStub
import com.crashnote.servlet.collect._
import com.crashnote.servlet.report.ServletReporter
import com.crashnote.core.report.impl.ThrowableLogEvt

class ServletReporterSpec
    extends MockSpec[ServletReporter[ConfigStub]] {

    var m_session: ILogSession = _
    var m_processor: Processor[C] = _
    var m_reqColl: RequestCollector = _
    var m_sesColl: SessionCollector = _

    "Reporter" should {

        "start session before request" >> new Started() {
            target.beforeRequest(null)

            expect {
                one(m_session).clear()
            }
        }

        "handle uncaught exception - but do NOT flush session" >> new Started() {
            val req = mock[HttpServletRequest]
            m_session.isEmpty returns false
            target.uncaughtException(req, Thread.currentThread(), new RuntimeException("oops"))

            expect {
                one(m_session).addEvent(any[ThrowableLogEvt])
                verifyUntouched(m_processor)
                no(m_session).clear()
            }
        }

        "end session after request" >> new Started() {
            val req = mock[HttpServletRequest]
            target.afterRequest(req, null)

            expect {
                one(m_reqColl).collect(req)
                one(m_sesColl).collect(req)
            }
        }
    }

    def configure(config: C) = {
        config.isEnabled returns true
        new ServletReporter[C](config)
    }

    override def mock() {
        m_session = _mock[ILogSession]
        m_processor = _mock[Processor[C]]
        m_reqColl = _mock[RequestCollector]
        m_sesColl = _mock[SessionCollector]
    }

    override def afterStarted() {
        reset(m_session, m_processor)
    }
}