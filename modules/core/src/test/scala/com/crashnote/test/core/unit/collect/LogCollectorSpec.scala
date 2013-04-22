/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
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
package com.crashnote.test.core.unit.collect

import scala.collection.JavaConversions._
import com.crashnote.core.collect.impl._
import com.crashnote.core.collect.impl.ExcpCollector
import com.crashnote.core.model.log.LogEvt
import com.crashnote.core.build.Builder
import com.crashnote.core.model.types.LogLevel
import com.crashnote.core.build.impl.JSONDataArray
import com.crashnote.core.model.data._
import com.crashnote.test.core.defs.TargetMockSpec

class LogCollectorSpec
  extends TargetMockSpec[LogCollector] {

  var m_evt: LogEvt[_] = _
  var m_excpColl: ExcpCollector = _

  "Log Collector" should {

    "collect log events" >> new Mock() {
      val evts = List(m_evt, m_evt)
      val res = target.collect(evts)

      res.size() === 2
      val log1 = res.get(0).asInstanceOf[DataObject]
      val log2 = res.get(1).asInstanceOf[DataObject]

      log1.get("message") === "oops"
      log1.get("occurredAt") === "2000-01-01T06:00Z"
      log1.get("source") === "com.example"
      log1.get("thread") === "main"
      log1.get("level") === "CRASH"
      log1.get("exceptions") !== null

      val logArgs = log1.get("messageArgs").asInstanceOf[DataArray]
      logArgs.get(0) === "abc"
      logArgs.get(1) === "xyz"

      val logCtx = log1.get("context").asInstanceOf[DataObject]
      logCtx.get("key1") === "val"
      logCtx.get("key2").toString === "2"
    }
  }

  // SETUP ======================================================================================

  def configure(config: C) = {
    config.getBuilder returns new Builder
    new LogCollector(config)
  }

  override def mock() {
    m_evt = mock[LogEvt[_]]
    m_evt.getMessage returns "oops"
    m_evt.getTimeStamp returns 946706400000L
    m_evt.getLoggerName returns "com.example"
    m_evt.getThreadName returns "main"
    m_evt.getThrowable returns newExcp()
    m_evt.getLevel returns LogLevel.CRASH
    m_evt.getMDC returns Map("key1" -> "val", "key2" -> new java.lang.Long(2))
    m_evt.getArgs returns Array("abc", "xyz")

    m_excpColl = _mock[ExcpCollector]
    m_excpColl.collect(anyThrowable) returns {
      val a = new JSONDataArray()
      a.add("test")
      a
    }
  }
}