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

import com.crashnote.core.report.impl.session.LocalLogSession
import com.crashnote.core.model.log.ILogSession
import com.crashnote.test.core.util.FactoryUtil
import com.crashnote.test.core.defs.TargetMockSpec

class LocalLogSessionSpec
    extends TargetMockSpec[LocalLogSession] with FactoryUtil {

    "Local Log Session" should {

        "copy" >> {
            val s = new LocalLogSession()
            s.addEvent(newLogEvt())
            val copy = s.copy()

            copy !== null
            copy.getEvents.size() === 1
        }

        "delegate" >> {
            "clear" >> new Mock() {
                target.clear()
                there was one(m_session).clear()
            }
            "context method" >> {
                "put" >> new Mock() {
                    target.putCtx("test", "data")
                    there was one(m_session).putCtx("test", "data")
                }
                "remove" >> new Mock() {
                    target.removeCtx("test")
                    there was one(m_session).removeCtx("test")
                }
                "clear" >> new Mock() {
                    target.clearCtx()
                    there was one(m_session).clearCtx()
                }
                "get" >> new Mock() {
                    target.getContext
                    there was one(m_session).getContext
                }
                "has" >> new Mock() {
                    target.hasContext
                    there was one(m_session).hasContext
                }
            }
            "event method" >> {
                "get" >> new Mock() {
                    target.getEvents
                    there was one(m_session).getEvents
                }
                "add" >> new Mock() {
                    val evt = newLogEvt()
                    target.addEvent(evt)
                    there was one(m_session).addEvent (evt)
                }
                "clear" >> new Mock() {
                    target.clearEvents()
                    there was one(m_session).clearEvents
                }
                "has" >> new Mock() {
                    target.isEmpty
                    there was one(m_session).isEmpty
                }
            }
        }
    }

    // SETUP ======================================================================================

    var m_session: ILogSession = _

    def configure(config: C) =
        new LocalLogSession() {
            override protected def getSession = m_session
        }

    override def mock() {
        m_session = mock[ILogSession]
    }

}