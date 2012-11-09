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
package com.crashnote.test.servlet.unit

import javax.servlet._

import com.crashnote.logger.helper.AutoLogConnector
import com.crashnote.servlet.report.ServletReporter
import com.crashnote.servlet.CrashnoteFilter
import http.{HttpServletResponse, HttpServletRequest}
import com.crashnote.test.servlet.defs.TargetMockSpec

class CrashnoteFilterSpec
    extends TargetMockSpec[CrashnoteFilter] {

    var m_reporter: ServletReporter = _
    var m_connector: AutoLogConnector = _

    var m_request: HttpServletRequest = _
    var m_response: HttpServletResponse = _
    var m_chain: FilterChain = _

    "Filter" should {

        "init" >> {
            "outside of AppEngine" >> {
                "when enabled" >> new Mock(ENABLED) {
                    target.init(m_fconf)

                    expect {
                        one(m_reporter).start()
                        one(m_connector).start()
                    }
                }
                "but skipped when disabled" >> new Mock(DISABLED) {
                    target.init(m_fconf)

                    expect {
                        no(m_reporter).start()
                        no(m_connector).start()
                    }
                }
            }

            "but not inside of AppEngine" >> new Mock {
                withSysProp("com.google.appengine.runtime.environment", "dev") {
                    target.init(m_fconf) must throwA[RuntimeException]
                }
            }
        }

        "filter" >> {
            "when NO error occurs" >> new Mock(ENABLED) {
                target.init(m_fconf)
                target.doFilter(m_request, m_response, m_chain)

                there was one(m_reporter).beforeRequest(m_request) then
                    one(m_reporter).afterRequest(m_request)
            }
            "when an error occurs" >> new Mock(ENABLED) {
                val err = new ServletException("oops")
                m_chain.doFilter(m_request, m_response) throws err

                target.init(m_fconf)
                target.doFilter(m_request, m_response, m_chain) must throwA[ServletException]

                there was one(m_reporter).beforeRequest(m_request) then
                    one(m_reporter).uncaughtException(m_request, Thread.currentThread(), err) then
                    one(m_reporter).afterRequest(m_request)
            }
            "just proceed with chain if disabled" >> new Mock(DISABLED) {
                target.init(m_fconf)
                target.doFilter(m_request, m_response, m_chain)

                expect {
                    one(m_chain).doFilter(m_request, m_response)
                    verifyUntouched(m_reporter)
                }
            }
        }

        "destroy" >> new Mock(ENABLED) {
            target.init(m_fconf)
            target.destroy()

            expect {
                one(m_reporter).stop()
                one(m_connector).stop()
            }
        }
    }

    // SETUP ======================================================================================

    var m_fconf: FilterConfig = _
    
    def configure(config: C) = {
        m_reporter = mock[ServletReporter]
        m_connector = mock[AutoLogConnector]
        m_request = mock[HttpServletRequest]
        m_response = mock[HttpServletResponse]
        m_chain = mock[FilterChain]

        m_conf.getReporter returns m_reporter
        m_conf.getLogConnector(any[ServletReporter]) returns m_connector

        new CrashnoteFilter() {
            override protected def getConfig(fc: FilterConfig) = m_conf
        }
    }
}