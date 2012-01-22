package com.crashnote.appengine.test.unit.send

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

import com.crashnote.appengine.config.AppengineConfig
import com.crashnote.appengine.test.defs.MockSpec
import com.crashnote.appengine.util.AppengineUtil
import com.crashnote.appengine.send.AppengineSender
import com.crashnote.core.model.log.LogReport
import com.google.appengine.api.urlfetch._

class AppengineSenderSpec
    extends MockSpec[AppengineSender[AppengineConfig]] {

    var m_report: LogReport = _
    var m_request: HTTPRequest = _
    var m_appengineUtil: AppengineUtil = _

    "AppEngine Sender" should {

        "send" >> new Mocked() {
            target.send(m_report)

            there was one(m_request).setPayload("DATA".getBytes("UTF-8")) then
                one(m_appengineUtil).fetchAsync(m_request)
        }
    }

    def configure(config: C) = {
        config.getPostUrl returns "http://api.com"
        config.getConnectionTimeout returns 10
        config.getClientInfo returns "CN"
        new AppengineSender(config)
    }

    override def mock() {
        m_report = mock[LogReport]
        m_report.toString returns "DATA"

        m_request = mock[HTTPRequest]
        m_appengineUtil = _mock[AppengineUtil]
        m_appengineUtil.createRequest(anyString, any[HTTPMethod], any[FetchOptions]) returns m_request
    }
}