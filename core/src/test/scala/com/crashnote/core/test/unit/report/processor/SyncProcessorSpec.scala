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
package com.crashnote.core.test.unit.report.processor

import com.crashnote.core.test.defs.MockSpec
import com.crashnote.core.report.impl.processor.impl.SyncProcessor
import com.crashnote.core.send.Sender
import com.crashnote.core.collect.Collector
import com.crashnote.core.report.impl.session.SharedLogSession
import com.crashnote.core.model.log.LogReport
import com.crashnote.core.test.defs.stubs.ConfigStub

class SyncProcessorSpec
    extends MockSpec[SyncProcessor[ConfigStub]] {

    var m_sender: Sender[C] = _
    var m_collector: Collector[C] = _

    "Sync Processor" should {

        "have lifecycle" >> {
            "start" >> new Mocked() {
                target.start()
                target.start()

                there was one(m_collector).start()
            }
            "stop" >> new Started() {
                target.stop()
                target.stop()

                there was one(m_collector).stop
            }
        }

        "process a session" >> new Started() {
            val s = new SharedLogSession()
            target.process(s)

            expect {
                one(m_collector).collectLog(s)
                one(m_sender).send(any[LogReport])
            }
        }
    }

    def configure(config: C) =
        new SyncProcessor[C](config)

    override def mock() {
        m_sender = _mock[Sender[C]]
        m_collector = _mock[Collector[C]]
    }
}