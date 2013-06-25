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
package com.crashnote.test.servlet.unit.config

import org.specs2.specification.BeforeExample
import com.crashnote.test.base.defs.MockSpec
import com.crashnote.servlet.report.ServletReporter
import com.crashnote.servlet.config.{ServletConfigFactory, ServletConfig}
import com.crashnote.core.config.ConfigLoader
import com.crashnote.test.servlet.util.FactoryUtil
import com.crashnote.web.config.WebConfig

class ServletConfigSpec
  extends MockSpec with BeforeExample with FactoryUtil {

  "Servlet Config" should {

    "inherit from Web Config" >> {
      c must haveSuperclass[WebConfig]
    }

    "act as factory" >> {
      "for servlet reporter" >> {
        c.getReporter must haveClass[ServletReporter[ServletConfig]]
      }
    }
  }

  // SETUP ======================================================================================

  var c: ServletConfig = _

  def before {
    c = getConfig("key" -> "0000000-00000-0000-0000-000000000000", "projectId" -> "42")
  }

  def getConfig(m: (String, Any)*) = {
    val loader = new ConfigLoader
    val m_loader = spy(loader)
    m_loader.fromSystemProps() returns loader.fromProps(toConfProps(m.toList), "spec")

    val m_filterConf = filterConfDefaultMock()

    val c = (new ServletConfigFactory[ServletConfig](m_filterConf, m_loader)).get
    c
  }
}