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
package com.crashnote.test.appengine.unit.collect

import com.crashnote.appengine.collect.AppengineCollector
import com.crashnote.appengine.collect.impl.{AppengineLogCollector, AppengineEnvCollector}
import com.crashnote.appengine.config.AppengineConfig
import com.crashnote.test.base.defs._

class AppengineCollectorSpec
    extends BaseMockSpec[AppengineCollector] {

    "AppEngine Collector" should {

        val m_conf = mock[AppengineConfig]

        "override default environment collector" >> {
            val r = new AppengineCollector(m_conf)

            r.getEnvCollector must haveClass[AppengineEnvCollector]
        }

        "override default log collector" >> {
            val r = new AppengineCollector(m_conf)

            r.getLogCollector must haveClass[AppengineLogCollector]
        }
    }

}