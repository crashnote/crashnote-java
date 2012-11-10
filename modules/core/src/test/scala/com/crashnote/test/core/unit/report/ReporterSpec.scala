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
package com.crashnote.test.core.unit.report

import com.crashnote.core.report.Reporter
import com.crashnote.core.report.impl.processor.Processor
import com.crashnote.core.model.log.ILogSession
import com.crashnote.core.report.impl.session._
import com.crashnote.core.report.impl.processor.impl.{AsyncProcessor, SyncProcessor}
import com.crashnote.core.report.impl.ThrowableLogEvt
import com.crashnote.test.core.defs.TargetMockSpec

class ReporterSpec
    extends TargetMockSpec[Reporter] {

    "Reporter" should {

        "have lifecycle" >> {
            "with 1 start" >> new Mock(ENABLED) {
                // execute
                target.start()
                target.start()

                // verify
                expect {
                    one(m_session).clear()
                    one(m_processor).start()
                }
                target.isStarted === true
            }
            "with 1 stop" >> new Started(ENABLED) {
                m_session.isEmpty returns false

                // execute
                target.stop()
                target.stop()

                // verify
                expect {
                    one(m_session).clear()
                    one(m_processor).process(m_session)
                    one(m_processor).stop()
                }
                target.isStarted === false
            }
        }

        "manage session" >> {
            "start" >> {
                "when operable" >> new Started(ENABLED) {
                    target.startSession()
                    there were one(m_session).clear()
                }
                "but skip when not enabled" >> new Started(DISABLED) {
                    target.startSession()
                    verifyUntouched(m_session)
                }
                "but skip when not started" >> new Configured(ENABLED) {
                    target.startSession()
                    verifyUntouched(m_session)
                }
            }
        }

        "manage log context" >> {
            "put" >> {
                "when operable" >> new Started(ENABLED) {
                    target.put("data", "test")
                    there was one(m_session).putCtx("data", "test")
                }
                "but skip when disabled" >> new Started(DISABLED) {
                    target.put("data", "test")
                    verifyUntouched(m_session)
                }
                "but skip when not started" >> new Configured(ENABLED) {
                    target.put("data", "test")
                    verifyUntouched(m_session)
                }
            }
            "remove" >> {
                "when operable" >> new Started(ENABLED) {
                    target.remove("test")
                    there was one(m_session).removeCtx("test")
                }
                "but skip when disabled" >> new Started(DISABLED) {
                    target.remove("test")
                    verifyUntouched(m_session)
                }
                "but skip when not started" >> new Configured(ENABLED) {
                    target.remove("test")
                    verifyUntouched(m_session)
                }
            }
            "clear" >> {
                "when operable" >> new Started(ENABLED) {
                    target.clear()
                    there was one(m_session).clear()
                }
                "but skip when disabled" >> new Started(DISABLED) {
                    target.clear()
                    verifyUntouched(m_session)
                }
                "but skip when not started" >> new Configured(ENABLED) {
                    target.clear()
                    verifyUntouched(m_session)
                }
            }
        }

        "report log event" >> {
            "when operable" >> new Started(ENABLED) {
                m_session.isEmpty returns false
                val evt = newLogEvt()
                target.reportLog(evt)
                expect {
                    one(m_session).addEvent(evt)
                    one(m_processor).process(m_session)
                    one(m_session).clear()
                }
            }
            "but skip when disabled" >> new Started(DISABLED) {
                target.clear()
                verifyUntouched(m_session)
            }
            "but skip when not started" >> new Configured(ENABLED) {
                target.clear()
                verifyUntouched(m_session)
            }
        }

        "handle uncaught exceptions" >> {
            "when unregistered" >> new Started(ENABLED) {
                m_session.isEmpty returns false
                target.uncaughtException(Thread.currentThread(), newExcp())

                expect {
                    one(m_session).addEvent(any[ThrowableLogEvt])
                    one(m_processor).process(m_session)
                    one(m_session).clear()
                }
            }
            "when registered" >> new Started(ENABLED) {
                // prepare
                val th = newExcp()
                val t = Thread.currentThread()
                m_session.isEmpty returns false
                Thread.setDefaultUncaughtExceptionHandler(m_excpHandler)

                target.registerAsDefaultExcpHandler()
                Thread.getDefaultUncaughtExceptionHandler === target

                // execute
                target.uncaughtException(t, th)

                // verify
                expect {
                    one(m_session).addEvent(any[ThrowableLogEvt])
                    one(m_processor).process(m_session)
                    one(m_session).clear()
                }
                there was one(m_excpHandler).uncaughtException(t, th)

                target.unregisterAsDefaultExcpHandler()
                Thread.getDefaultUncaughtExceptionHandler === m_excpHandler
            }
            "but skip when disabled" >> new Started(DISABLED) {
                target.uncaughtException(Thread.currentThread(), newExcp())
                verifyUntouched(m_session)
            }
            "but skip when not started" >> new Configured(ENABLED) {
                target.uncaughtException(Thread.currentThread(), newExcp())
                verifyUntouched(m_session)
            }
        }

        "create internal" >> {
            "session" >> {
                target.getSession must haveClass[LocalLogSession]
            }
            "processor" >> {
                "when in sync mode" >> new Configured(SYNC) {
                    target.getProcessor must haveClass[SyncProcessor]
                }
                "when in async mode" >> new Configured(ASYNC) {
                    target.getProcessor must haveClass[AsyncProcessor]
                }
            }
        }
    }

    // SETUP ======================================================================================

    var m_session: ILogSession = _
    var m_processor: Processor = _
    var m_excpHandler: Thread.UncaughtExceptionHandler = _

    def configure(config: C) =
        new Reporter(config)

    override def mock() {
        m_session = _mock[ILogSession]
        m_processor = _mock[Processor]
        m_excpHandler = _mock[Thread.UncaughtExceptionHandler]
    }

    override def afterStarted() {
        reset(m_session, m_processor)
    }

}