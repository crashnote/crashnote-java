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

import com.sun.security.auth.UnixPrincipal
import javax.servlet.http.HttpServletRequest
import com.crashnote.core.build.Builder
import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.servlet.collect.RequestCollector
import com.crashnote.core.model.data._

class RequestCollectorSpec
    extends MockSpec[RequestCollector] {

    "Request Collector" should {

        "collect" >> new Mocked() {

            val m_req = mock[HttpServletRequest]

            m_req.getMethod returns "PUT"
            m_req.getRequestURL returns new StringBuffer("http://test.com")
            m_req.getRemoteAddr returns "127.0.0.1"
            m_req.getUserPrincipal returns new UnixPrincipal("admin")

            m_req.getHeaderNames.asInstanceOf[javaEnum[String]] returns toEnum(List("User-Agent", "Accept"))
            m_req.getHeaders("Accept").asInstanceOf[javaEnum[String]] returns toEnum(List("text/plain", "text/html"))
            m_req.getHeaders("User-Agent").asInstanceOf[javaEnum[String]] returns toEnum(List("Googlebot"))

            m_req.getParameterNames.asInstanceOf[javaEnum[String]] returns toEnum(List("userName", "userPassword"))
            m_req.getParameter("userName") returns "stephen"
            m_req.getParameter("userPassword") returns "secret"

            val res = target.collect(m_req)
            res.get("mth") === "PUT"
            res.get("url") === "http://test.com"
            res.get("iph") === 6279231751978338320L
            res.get("principal") === "admin"

            val params = res.get("param").asInstanceOf[DataObject]
            params.size() === 2
            params.get("userName") === "stephen"
            params.get("userPassword") === "#"

            val headers = res.get("header").asInstanceOf[DataObject]
            headers.size() === 2
            headers.get("User-Agent") === "Googlebot"
            val acceptHeader = headers.get("Accept").asInstanceOf[DataArray]
            acceptHeader.get(0) === "text/plain"
            acceptHeader.get(1) === "text/html"
        }
    }

    def configure(config: C) = {
        config.getRequestFilters returns Array(".*password.*")
        config.getSkipHeaderData returns false
        config.getBuilder returns new Builder

        new RequestCollector(config)
    }
}