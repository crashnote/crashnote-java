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

import com.crashnote.core.report.impl.session.LogHeap
import com.crashnote.test.core.util.FactoryUtil
import com.crashnote.test.base.defs.UnitSpec

class LogHeapSpec
    extends UnitSpec with FactoryUtil {

    "Log Heap" should {

        "be instantiable" >> {
            val h = new LogHeap()

            h.isEmpty === true
            h.getSize === 0
        }

        "copy" >> {
            val h = new LogHeap()
            h.addEvt(newLogEvt())

            val copy = new LogHeap(h)
            copy.getSize === 1
        }

        "add and remove events" >> {
            val h = new LogHeap()

            val evt = newLogEvt()
            h.addEvt(evt)

            h.isEmpty === false
            h.getSize === 1
            h.getEvents !== null

            h.clear()

            h.isEmpty === true
            h.getSize === 0
        }
    }

}