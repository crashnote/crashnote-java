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
package com.crashnote.servlet.test.unit.collect

import javax.servlet.http._
import com.crashnote.core.build.Builder
import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.core.model.data.DataObject
import com.crashnote.servlet.collect._

class SessionCollectorSpec
    extends MockSpec[ServletSessionCollector] {

    "Session Collector" should {

        "collect" >> {

            "default" >> new Mocked() {
                val res = target.collect(mockReq())
                res.get("id") === "666"
                res.get("started_at") === 123456789L
                res.get("data") === null
            }

            "with session" >> new Mocked(WITH_SESSION) {
                val res = target.collect(mockReq())
                val sesData = res.get("data").asInstanceOf[DataObject]
                sesData !== null
                sesData.get("name") === "test"
                sesData.get("email") === "test@test.com"
            }
        }
    }

    override def mockConfig(): C = {
        val config = super.mockConfig()
        config.getSkipSessionData returns true
        config.getBuilder returns new Builder
    }

    def configure(config: C) =
        new ServletSessionCollector(config)

    def mockReq() = {
        val m_ses = mock[HttpSession]
        m_ses.getId returns "666"
        m_ses.getCreationTime returns 123456789L
        m_ses.getAttributeNames.asInstanceOf[javaEnum[String]] returns toEnum(List("name", "email"))
        m_ses.getAttribute("name") returns "test"
        m_ses.getAttribute("email") returns "test@test.com"

        val res = mock[HttpServletRequest]
        res.getSession returns m_ses
        res
    }

    lazy val WITH_SESSION = (config: C) => config.getSkipSessionData returns false
}