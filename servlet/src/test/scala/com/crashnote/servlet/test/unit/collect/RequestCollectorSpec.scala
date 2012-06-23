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

import scala.collection.JavaConversions._
import com.sun.security.auth.UnixPrincipal
import javax.servlet.http.HttpServletRequest
import com.crashnote.core.build.Builder
import com.crashnote.servlet.test.defs.MockSpec
import com.crashnote.servlet.collect.RequestCollector
import com.crashnote.core.model.data._

class RequestCollectorSpec
    extends MockSpec[RequestCollector] {

    "Request Collector" should {

        "collect" >> {

            "by default" >> new Mocked() {
                val res = target.collect(mockReq())
                res.get("method") === "PUT"
                res.get("url") === "http://test.com"
                res.get("ip_hash") === 6279231751978338320L
                res.get("principal") === "admin"

                val params = res.get("parameters").asInstanceOf[DataObject]
                params.size() === 3
                params.get("userName") === "stephen"
                params.get("userPassword") === "#"
                params.get("userBio") === "I was born"

                val headers = res.get("headers").asInstanceOf[DataObject]
                headers.size() === 2
                headers.get("User-Agent") === "Googlebot"
                val acceptHeader = headers.get("Accept").asInstanceOf[DataArray]
                acceptHeader.get(0) === "text/plain"
                acceptHeader.get(1) === "text/html"
            }

            "IP address" >> new Mocked(WITH_IP) {
                val res = target.collect(mockReq())
                res.get("ip") === "127.0.0.1"
            }

            "no header data" >> new Mocked(WITHOUT_HEADER) {
                val res = target.collect(mockReq())
                res.get("headers") === null
            }
        }
    }

    override def mockConfig(): C = {
        val config = super.mockConfig()
        config.getRequestFilters returns List(".*password.*")
        config.getMaxRequestParameterSize returns 10
        config.getSkipHeaderData returns false
        config.getHashRemoteIP returns true
        config.getBuilder returns new Builder
        config
    }

    def configure(config: C) =
        new RequestCollector(config)

    def mockReq() = {
        val res = mock[HttpServletRequest]

        res.getMethod returns "PUT"
        res.getRequestURL returns new StringBuffer("http://test.com")
        res.getRemoteAddr returns "127.0.0.1"
        res.getUserPrincipal returns new UnixPrincipal("admin")

        res.getHeaderNames.asInstanceOf[javaEnum[String]] returns toEnum(List("User-Agent", "Accept"))
        res.getHeaders("Accept").asInstanceOf[javaEnum[String]] returns toEnum(List("text/plain", "text/html"))
        res.getHeaders("User-Agent").asInstanceOf[javaEnum[String]] returns toEnum(List("Googlebot"))

        res.getParameterNames.asInstanceOf[javaEnum[String]] returns toEnum(List("userName", "userPassword", "userBio"))
        res.getParameter("userName") returns "stephen"
        res.getParameter("userPassword") returns "secret"
        res.getParameter("userBio") returns "I was born in Berlin, Germany and grew up..."

        res
    }

    lazy val WITH_IP = (config: C) => config.getHashRemoteIP returns false
    lazy val WITHOUT_HEADER = (config: C) => config.getSkipHeaderData returns true
}