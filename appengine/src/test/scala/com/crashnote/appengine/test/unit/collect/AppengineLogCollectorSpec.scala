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
package com.crashnote.appengine.test.unit.collect

import scala.collection.JavaConversions._
import com.crashnote.appengine.test.defs.MockSpec
import com.crashnote.appengine.config.AppengineConfig
import com.crashnote.core.build.Builder
import com.crashnote.appengine.collect.impl._
import com.crashnote.core.model.data.DataObject
import com.crashnote.core.model.log.LogEvt
import com.crashnote.core.model.types.LogLevel

class AppengineLogCollectorSpec
    extends MockSpec[AppengineLogCollector[AppengineConfig]] {

    "AppEngine Log Collector" should {

        "filter AppEngine prefix for logger name" >> {

            "when present" >> new Mocked() {
                val evt = evtWithSource("[s~crashnote/demo.356739207231993312].com.example")
                val res = target.collect(List(evt))
                val log = res.get(0).asInstanceOf[DataObject]

                log.get("source") === "com.example"
            }
            "when present and root package" >> new Mocked() {
                val evt = evtWithSource("[s~crashnote/demo.356739207231993312]")
                val res = target.collect(List(evt))
                val log = res.get(0).asInstanceOf[DataObject]

                log.get("source") === ""
            }
            "skip when not present" >> new Mocked() {
                val evt = evtWithSource("com.example")
                val res = target.collect(List(evt))
                val log = res.get(0).asInstanceOf[DataObject]

                log.get("source") === "com.example"
            }
            "skip when empty" >> new Mocked() {
                val evt = evtWithSource(null)
                val res = target.collect(List(evt))
                val log = res.get(0).asInstanceOf[DataObject]

                log.get("source") === null
            }
        }
    }

    def configure(config: C) = {
        config.getBuilder returns new Builder()
        new AppengineLogCollector[C](config)
    }

    private def evtWithSource(src: String) = {
        val evt = mock[LogEvt[_]]
        evt.getLevel returns LogLevel.CRASH
        evt.getLoggerName returns src
        evt
    }
}