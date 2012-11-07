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
package com.crashnote.test.servlet.unit.config

import org.specs2.specification.BeforeExample
import com.crashnote.test.base.defs.MockSpec
import com.crashnote.servlet.report.ServletReporter
import com.crashnote.servlet.config.{ServletConfigFactory, ServletConfig}
import javax.servlet.FilterConfig

class ServletConfigSpec
    extends MockSpec with BeforeExample {

    var c: ServletConfig = _

    "Servlet Config" should {

        "act as factory" >> {
            "for servlet reporter" >> {
                c.getReporter must haveClass[ServletReporter]

            }
        }
    }

    def before {
        val fc = mock[FilterConfig]
        c = (new ServletConfigFactory[ServletConfig](fc)).get()
    }
}