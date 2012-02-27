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
package com.crashnote.core.test.unit.report.session

import com.crashnote.core.test.defs._
import com.crashnote.core.report.impl.session.LocalLogSession
import com.crashnote.core.model.log.ILogSession
import com.crashnote.core.test.util.FactoryUtil

class LocalLogSessionSpec
    extends MockSpec[LocalLogSession] with FactoryUtil {

    var m_session: ILogSession = _

    "Local Log Session" should {

        "copy" >> {
            val s = new LocalLogSession()
            s.addEvent(newLogEvt())
            val copy = s.copy()

            copy !== null
            copy.getEvents.size() === 1
        }

        "delegate" >> {
            "clear" >> new Mocked() {
                target.clear()
                there was one(m_session).clear()
            }
            "context method" >> {
                "put" >> new Mocked() {
                    target.putCtx("test", "data")
                    there was one(m_session).putCtx("test", "data")
                }
                "remove" >> new Mocked() {
                    target.removeCtx("test")
                    there was one(m_session).removeCtx("test")
                }
                "clear" >> new Mocked() {
                    target.clearCtx()
                    there was one(m_session).clearCtx()
                }
                "get" >> new Mocked() {
                    target.getContext
                    there was one(m_session).getContext
                }
                "has" >> new Mocked() {
                    target.hasContext
                    there was one(m_session).hasContext
                }
            }
            "event method" >> {
                "get" >> new Mocked() {
                    target.getEvents
                    there was one(m_session).getEvents
                }
                "add" >> new Mocked() {
                    val evt = newLogEvt()
                    target.addEvent(evt)
                    there was one(m_session).addEvent (evt)
                }
                "clear" >> new Mocked() {
                    target.clearEvents()
                    there was one(m_session).clearEvents
                }
                "has" >> new Mocked() {
                    target.hasEvents
                    there was one(m_session).hasEvents
                }
            }
        }
    }

    def configure(config: C) =
        new LocalLogSession() {
            override protected def getSession = m_session
        }

    override def mock() {
        m_session = mock[ILogSession]
    }

}