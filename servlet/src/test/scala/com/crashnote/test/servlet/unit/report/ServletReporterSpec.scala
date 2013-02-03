/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crashnote.test.servlet.unit.report

import javax.servlet.http.HttpServletRequest

import com.crashnote.core.report.impl.processor.Processor
import com.crashnote.core.model.log.ILogSession
import com.crashnote.test.servlet.defs.TargetMockSpec
import com.crashnote.servlet.report.ServletReporter
import com.crashnote.servlet.config.ServletConfig

class ServletReporterSpec
  extends TargetMockSpec[ServletReporter[ServletConfig]] {

  "Servlet Reporter" should {

    "ignore requests" >> {
      "from local" >> new Started(IGNORE_LOCAL_REQ) {
        target.beforeRequest(req("127.0.0.1"))
        expect {
          verifyUntouched(m_session)
        }
      }
    }

    "accept requests" >> {
      "from remote" >> new Started(IGNORE_LOCAL_REQ) {
        target.beforeRequest(req("192.168.0.1"))
        expect {
          one(m_session).clear()
        }
      }
      "from local - when config enables it" >> new Started(ACCEPT_LOCAL_REQ) {
        target.beforeRequest(req("127.0.0.1"))
        expect {
          one(m_session).clear()
        }
      }
    }
  }

  // SETUP ======================================================================================

  var m_req: HttpServletRequest = _
  var m_session: ILogSession = _
  var m_processor: Processor = _

  def configure(config: C) = {
    config.isEnabled returns true
    new ServletReporter(config)
  }

  override def mock() {
    m_session = _mock[ILogSession]
    m_processor = _mock[Processor]
  }

  override def afterStarted() {
    reset(m_session)
  }

  def req(addr: String) = {
    val m_req = mock[HttpServletRequest]
    m_req.getRemoteAddr returns addr
    m_req.getRequestURL returns new StringBuffer("crashnote.com")
    m_req
  }
}