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
package com.crashnote.test.core.unit.config

import com.crashnote.core.config.{ConfigLoader, CrashConfigFactory, CrashConfig}
import com.crashnote.test.core.defs.TargetMockSpec
import com.crashnote.test.base.defs.MockSpec

class ConfigFactorySpec
    extends MockSpec {

    "Config Factory" should {

        "create configuration instance" >> {

            val m_loader = spy(new ConfigLoader)
            val factory = new CrashConfigFactory[CrashConfig](m_loader)

            // enable debug mode
            System.setProperty("crashnote.debug", "true")

            // == first call
            var c: CrashConfig = null
            var (out, _) = capture { c = factory.get() }

            c.isDebug === true
            out must contain(""""crashnote""")

            there was one(m_loader).fromSystemProps() then
                one(m_loader).fromEnvProps() then
                one(m_loader).fromFile("crashnote.about") then
                one(m_loader).fromFile("crashnote") then
                atLeastOne(m_loader).fromFile("crashnote.default")

            println(out)

            // == second call (should be cached)
            out = capture { c = factory.get() }._1

            out must beEmpty
            noMoreCallsTo(m_loader)
        }
    }
}