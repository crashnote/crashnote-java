package com.crashnote.servlet.test.unit

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

import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.servlet.report.ServletReporter
import com.crashnote.logger.helper.AutoLogConnector
import com.crashnote.servlet.CrashnoteFilter
import com.crashnote.servlet.test.defs.stubs.ConfigStub
import javax.servlet._

class CrashnoteFilterSpec
    extends MockSpec[CrashnoteFilter] {

    var m_conf: ConfigStub = _
    var m_reporter: ServletReporter[C] = _
    var m_connector: AutoLogConnector = _

    var m_request: ServletRequest = _
    var m_response: ServletResponse = _
    var m_chain: FilterChain = _

    "Filter" should {

        "init" >> {
            "when enabled" >> new Mocked(ENABLED) {
                target.init(null)

                expect {
                    one(m_reporter).start()
                    one(m_connector).start()
                }
            }
            "but skipped when disabled" >> new Mocked(DISABLED) {
                target.init(null)

                expect {
                    no(m_reporter).start()
                    no(m_connector).start()
                }
            }
        }

        "filter" >> {
            "when NO error occurs" >> new Mocked(ENABLED) {
                target.init(null)
                target.doFilter(m_request, m_response, m_chain)

                there was one(m_reporter).beforeRequest(m_request, m_response) then
                    one(m_reporter).afterRequest(m_request, m_response)
            }
            "when an error occurs" >> new Mocked(ENABLED) {
                val err = new ServletException("oops")
                m_chain.doFilter(m_request, m_response) throws err

                target.init(null)
                target.doFilter(m_request, m_response, m_chain) must throwA[ServletException]

                there was one(m_reporter).beforeRequest(m_request, m_response) then
                    one(m_reporter).uncaughtException(m_request, Thread.currentThread(), err) then
                    one(m_reporter).afterRequest(m_request, m_response)
            }
            "just proceed with chain if disabled" >> new Mocked(DISABLED) {
                target.init(null)
                target.doFilter(m_request, m_response, m_chain)

                expect {
                    one(m_chain).doFilter(m_request, m_response)
                    verifyUntouched(m_reporter)
                }
            }
        }

        "destroy" >> new Mocked(ENABLED) {
            target.init(null)
            target.destroy()

            expect {
                one(m_reporter).stop()
                one(m_connector).stop()
            }
        }
    }

    def configure(config: C) = {
        m_reporter = mock[ServletReporter[C]]
        m_connector = mock[AutoLogConnector]
        m_request = mock[ServletRequest]
        m_response = mock[ServletResponse]
        m_chain = mock[FilterChain]

        m_conf = config
        m_conf.getReporter returns m_reporter
        m_conf.getLogConnector(any[ServletReporter[C]]) returns m_connector

        new CrashnoteFilter() {
            override protected def getConfig(fConf: FilterConfig) = m_conf
        }
    }
}