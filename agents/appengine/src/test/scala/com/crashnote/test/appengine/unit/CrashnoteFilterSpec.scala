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
package com.crashnote.test.appengine.unit

import com.crashnote.appengine.CrashnoteFilter
import com.crashnote.test.appengine.defs.TargetMockSpec
import javax.servlet.FilterConfig

class CrashnoteFilterSpec
    extends TargetMockSpec[CrashnoteFilter] {

    "Crashnote Filter" should {

        "init" >> {

            "on AppEngine" >> new Mock {
                System.setProperty("com.google.appengine.runtime.environment", "dev")
                target.init(m_fconf) !== null
                System.clearProperty("com.google.appengine.runtime.environment")
            }
            "but not outside of AppEngine" >> new Mock {
                target.init(m_fconf) must throwA[RuntimeException]
            }
        }
    }

    // SETUP =====================================================================================

    var m_fconf: FilterConfig = _

    def configure(config: C) = {
        m_fconf = mock[FilterConfig]
        new CrashnoteFilter
    }
}