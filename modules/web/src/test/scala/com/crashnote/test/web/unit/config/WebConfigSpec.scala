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
package com.crashnote.test.web.unit.config

import com.crashnote.test.base.defs.MockSpec
import com.crashnote.core.config.ConfigLoader
import com.crashnote.web.config.{WebConfigFactory, WebConfig}

class WebConfigSpec
  extends MockSpec {

  "Web Config" should {

    "return" >> {

      "whether to hash IP" >> {
        val conf1 = getConfig("request.hash-ip" -> true)
        conf1.getHashRemoteIP === true

        val conf2 = getConfig("request.hash-ip" -> false)
        conf2.getHashRemoteIP === false
      }
      "whether to exclude header data" >> {
        val conf1 = getConfig("request.exclude-headers" -> true)
        conf1.getSkipHeaderData === true

        val conf2 = getConfig("request.exclude-headers" -> false)
        conf2.getSkipHeaderData === false
      }
      "whether to exclude session data" >> {
        val conf1 = getConfig("request.exclude-session" -> true)
        conf1.getSkipSessionData === true

        val conf2 = getConfig("request.exclude-session" -> false)
        conf2.getSkipSessionData === false
      }
      "whether to ignore localhost requests" >> {
        val conf1 = getConfig("request.ignore-localhost" -> true)
        conf1.getIgnoreLocalRequests === true

        val conf2 = getConfig("request.ignore-localhost" -> false)
        conf2.getIgnoreLocalRequests === false
      }
      "maximum paramter size" >> {
        val conf = getConfig("request.max-parameter-size" -> 100)
        conf.getMaxRequestParameterSize === 100
      }
      /*
      "request filter" >> {
          val conf = getConfig("filter.request" -> """["password", "creditcard"]""")
          conf.getRequestFilters.asScala must contain("password", "creditcard").only
      }
      */
    }
  }

  // SETUP ======================================================================================

  def getConfig(m: (String, Any)*) = {
    val m_loader = spy(new ConfigLoader)
    m_loader.fromSystemProps() returns
      (new ConfigLoader).fromProps(toConfProps(m.toList ::: List("key" -> "0000000-00000-0000-0000-000000000000", "projectId" -> "42")), "spec")

    (new WebConfigFactory[WebConfig](m_loader)).get
  }
}