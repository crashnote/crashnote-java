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
package com.crashnote.test.core.unit.report.session

import com.crashnote.core.report.impl.session.SharedLogSession
import com.crashnote.test.base.defs.UnitSpec
import com.crashnote.test.core.util.FactoryUtil

class SharedLogSessionSpec
    extends UnitSpec with FactoryUtil {

    "Shared Log Session" should {

        "manage events" >> {
            val s = new SharedLogSession
            s.isEmpty === true

            val evt = newLogEvt()
            s.addEvent(evt)

            s.isEmpty === false
            s.getEvents !== null

            s.clearEvents()
            s.isEmpty === true
        }
        "manage context" >> {
            val s = new SharedLogSession
            s.hasContext === false

            s.putCtx("test", "data")

            s.hasContext === true
            s.getContext !== null

            s.removeCtx("test")

            s.hasContext === false

            s.putCtx("test", "data")
            s.clearCtx()

            s.hasContext === false
        }

        "clear all" >> {
            val s = new SharedLogSession
            s.putCtx("test", "data")
            s.addEvent(newLogEvt())

            s.isEmpty === false
            s.hasContext === true

            s.clear()

            s.isEmpty === true
            s.hasContext === false
        }
    }

}