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
package com.crashnote.test.web.unit.collect

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.crashnote.test.web.defs.TargetMockSpec
import com.crashnote.web.collect.RequestCollector
import com.crashnote.test.web.util.HTTPRequest
import com.crashnote.core.build.impl.JSONDataObject
import com.crashnote.core.build.Builder
import com.crashnote.core.model.data.{DataArray, DataObject}

class RequestCollectorSpec
  extends TargetMockSpec[RequestCollector[HTTPRequest]] {

  "Request Collector" should {

    "collect" >> {
      "request data" >> new Configured {

        // execute
        val res = target.collect(req())
        println(res)

        // verify
        res.get("ip") must not beNull

        val params = res.get("parameters").asInstanceOf[DataObject]
        params must not beNull

        params.get("value") === "A"
        params.get("array-1") === "A"
        params.get("array-2").asInstanceOf[DataArray] === List("A", "B").asJava

        val headers = res.get("headers").asInstanceOf[DataObject]
        headers must not beNull

        headers.get("list-1") === "A"
        headers.get("list-2").asInstanceOf[DataArray] === List("A", "B").asJava
        headers.get("array-1") === "A"
        headers.get("array-2").asInstanceOf[DataArray] === List("A", "B").asJava
      }

      "IP address" >> new Mock(WITH_IP) {
        val res = target.collect(req())

        res.get("ip") === "127.0.0.1"
      }

      "no header data" >> new Mock(WITHOUT_HEADER) {
        val res = target.collect(req())

        res.get("headers") === null
      }
    }
  }

  // SETUP ======================================================================================

  override def mockConfig(): C = {
    val mconf = super.mockConfig()
    mconf.getBuilder returns new Builder
    mconf.getMaxRequestParameterSize returns 100
  }

  def configure(config: C) = {
    new RequestCollector[HTTPRequest](m_conf) {
      def collectReqBase(req: HTTPRequest) = {
        val obj = new JSONDataObject
        addIP(obj, "127.0.0.1")
        obj
      }

      def collectReqParams(req: HTTPRequest) = {
        val obj = new JSONDataObject
        addParam(obj, "value", "A")
        addParam(obj, "array-1", Array("A"))
        addParam(obj, "array-2", Array("A", "B"))
        obj
      }

      def collectReqHeader(req: HTTPRequest) = {
        val obj = new JSONDataObject
        addHeader(obj, "list-1", List("A"))
        addHeader(obj, "list-2", List("A", "B"))
        addHeader(obj, "array-1", Array("A"))
        addHeader(obj, "array-2", Array("A", "B"))
        obj
      }
    }
  }
}