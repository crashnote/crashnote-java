package com.crashnote.servlet.test.unit.collect

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

import javax.servlet.http._
import com.crashnote.core.build.Builder
import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.core.model.data.DataObject
import com.crashnote.servlet.collect._

class SessionCollectorSpec
    extends MockSpec[SessionCollector] {

    "Session Collector" should {

        "collect" >> new Mocked() {

            val m_ses = mock[HttpSession]
            m_ses.getId returns "666"
            m_ses.getCreationTime returns 123456789L
            m_ses.getAttributeNames.asInstanceOf[javaEnum[String]] returns toEnum(List("name", "email"))
            m_ses.getAttribute("name") returns "test"
            m_ses.getAttribute("email") returns "test@test.com"

            val m_req = mock[HttpServletRequest]
            m_req.getSession returns m_ses

            val res = target.collect(m_req)
            res.get("id") === "666"
            res.get("started") === 123456789L

            val sesData = res.get("data").asInstanceOf[DataObject]
            sesData.get("name") === "test"
            sesData.get("email") === "test@test.com"
        }
    }

    def configure(config: C) = {
        config.getSkipSessionData returns false
        config.getBuilder returns new Builder

        new SessionCollector(config)
    }
}