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
package com.crashnote.test.core.unit.report.processor

import java.util.concurrent._
import com.crashnote.core.report.impl.processor.impl.AsyncProcessor
import com.crashnote.core.report.impl.processor.Processor
import com.crashnote.core.report.impl.session.LocalLogSession
import com.crashnote.test.core.defs.stubs.ConfigStub
import com.crashnote.test.core.defs.MockSpec

class AsyncProcessorSpec
    extends MockSpec[AsyncProcessor[ConfigStub]] {

    var m_processor: Processor[C] = _
    var m_scheduler: ScheduledExecutorService = _

    "Async Processor" should {

        "have lifecycle" >> {
            "start" >> new Mock() {
                target.start() === true
                target.start() === true

                there was one(m_processor).start()
            }
            "stop" >> {
                "when everything runs smoothly" >> new Started() {
                    target.stop() === false
                    target.stop() === false

                    there was one(m_processor).stop
                }
                "when an exception occurs" >> new Started() {
                    m_scheduler.awaitTermination(anyLong, any[TimeUnit]) throws new InterruptedException("")
                    target.stop() === false

                    there was one(m_processor).stop
                }
            }
        }

        "process a session" >> new Started() {
            val s = new LocalLogSession()
            target.process(s)

            there was one(m_scheduler).submit(any[Callable[Void]])
        }
    }

    def configure(config: C) =
        new AsyncProcessor[C](config, m_processor)

    override def mock() {
        m_processor = _mock[Processor[C]]
        m_scheduler = _mock[ScheduledExecutorService]
    }
}