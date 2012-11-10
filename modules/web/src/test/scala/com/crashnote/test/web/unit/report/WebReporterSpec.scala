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
package com.crashnote.test.web.unit.report

import com.crashnote.test.web.defs.TargetMockSpec
import com.crashnote.web.config.WebConfig
import com.crashnote.web.report.WebReporter
import com.crashnote.web.collect.{SessionCollector, RequestCollector}
import com.crashnote.test.web.util.HTTPRequest
import com.crashnote.core.model.log.ILogSession
import com.crashnote.core.report.impl.processor.Processor
import com.crashnote.core.report.impl.ThrowableLogEvt

class WebReporterSpec
    extends TargetMockSpec[WebReporter[WebConfig, HTTPRequest]] {

    "Web Reporter" should {

        "have request lifecycle" >> {
            "#1 start session before request" >> new Started() {
                "init session" >> {
                    target.beforeRequest(req())

                    expect {
                        one(m_session).clear()
                    }
                }
                "skip when request ignored" >> {
                    target.beforeRequest(req(ignore = true))

                    expect {
                        verifyUntouched(m_session)
                    }
                }
            }

            "#2 handle uncaught exception" >> new Started() {
                target.uncaughtException(req(), thd(), excp())

                expect {
                    one(m_session).addEvent(any[ThrowableLogEvt])
                    verifyUntouched(m_processor)
                    no(m_session).clear()
                }
            }

            "#3 end session after request" >> new Started() {
                "collect data when session is non-empty" >> {
                    val r = req()
                    m_session.isEmpty returns true

                    // execute
                    target.afterRequest(r)

                    expect {
                        one(m_reqColl).collect(r)
                        one(m_sesColl).collect(r)
                        one(m_processor).process(any[ILogSession])
                        one(m_session).clear()
                    }
                }
                "skip when session is empty" >> {
                    m_session.isEmpty returns true

                    // execute
                    target.afterRequest(req())

                    expect {
                        verifyUntouched(m_reqColl, m_sesColl, m_processor, m_session)
                    }
                }
            }
        }
    }

    // SETUP ======================================================================================

    var m_session: ILogSession = _
    var m_processor: Processor = _
    var m_reqColl: RequestCollector[HTTPRequest] = _
    var m_sesColl: SessionCollector[HTTPRequest] = _

    override def mock() {
        m_reqColl = mock[RequestCollector[HTTPRequest]]
        m_sesColl = mock[SessionCollector[HTTPRequest]]
        m_session = _mock[ILogSession]
        m_processor = _mock[Processor]
    }

    def configure(config: C) = {
        config.isEnabled returns true

        new WebReporter[WebConfig, HTTPRequest](config) {
            protected def getRequestCollector(config: WebConfig) =
                m_reqColl

            protected def getSessionCollector(config: WebConfig) =
                m_sesColl

            protected def ignoreRequest(req: HTTPRequest) =
                req.ignore
        }
    }

    override def afterStarted() {
        reset(m_session, m_processor)
    }
}