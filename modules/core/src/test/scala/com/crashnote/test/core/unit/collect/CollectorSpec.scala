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
package com.crashnote.test.core.unit.collect

import com.crashnote.core.collect.Collector
import com.crashnote.core.collect.impl._
import com.crashnote.core.report.impl.session.LocalLogSession
import com.crashnote.core.build.Builder
import com.crashnote.test.core.defs.TargetMockSpec
import com.crashnote.test.core.util.FactoryUtil

class CollectorSpec
    extends TargetMockSpec[Collector] {

    var m_builder: Builder = _
    var m_envColl: EnvCollector = _
    var m_logColl: LogCollector = _

    "Collector" should {

        "have lifecycle" >> {
            "start" >> new Mock() {
                target.start() === true
                target.start() === true
            }
            "stop" >> new Started() {
                target.stop() === false
                target.stop() === false
            }
        }

        "collect session" >> {
            "with one event" >> new Started() {
                val s = new LocalLogSession()
                val evt = newLogEvt()
                s.addEvent(evt)

                val r = target.collectLog(s)

                expect {
                    one(m_logColl).collect(evt)
                    one(m_envColl).collect()
                }
            }
            "with multiple events" >> new Started() {
                val s = new LocalLogSession()
                s.addEvent(newLogEvt())
                s.addEvent(newLogEvt())
                val evts = s.getEvents

                val r = target.collectLog(s)

                expect {
                    one(m_logColl).collect(evts)
                    one(m_envColl).collect()
                }
            }
        }
    }

    // SETUP ======================================================================================

    def configure(config: C) = {
        config.getBuilder returns new Builder
        new Collector(config)
    }

    override def mock() {
        m_envColl = _mock[EnvCollector]
        m_logColl = _mock[LogCollector]
    }
}