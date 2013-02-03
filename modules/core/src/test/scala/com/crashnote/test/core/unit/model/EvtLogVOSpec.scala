/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
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
package com.crashnote.test.core.unit.model

import com.crashnote.core.model.log.{ILogEvt, LogEvtVO}
import com.crashnote.core.model.types.LogLevel
import com.crashnote.test.base.defs.MockSpec

class EvtLogVOSpec
  extends MockSpec {

  "Event Log VO" should {

    "instantiate" >> {

      val err = new RuntimeException("oops")

      // mock
      val m_evt = mock[ILogEvt]
      m_evt.getLoggerName returns "com.example"
      m_evt.getLevel returns LogLevel.ERROR
      m_evt.getThreadName returns "1"
      m_evt.getThrowable returns err
      m_evt.getMessage returns "oops"
      m_evt.getTimeStamp returns 123456789L

      // expect
      val r = new LogEvtVO(m_evt)
      r.getLoggerName === "com.example"
      r.getLevel === LogLevel.ERROR
      r.getThreadName === "1"
      r.getThrowable === err
      r.getMessage === "oops"
      r.getTimeStamp === 123456789L
    }
  }
}