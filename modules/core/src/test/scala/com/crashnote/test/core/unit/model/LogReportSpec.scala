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
package com.crashnote.test.core.unit.model

import com.crashnote.core.model.log.LogReport
import com.crashnote.core.build.impl.JSONDataObject
import com.crashnote.test.base.defs.UnitSpec

class LogReportSpec
  extends UnitSpec {

  "Log Report" should {

    var report: LogReport = null

    val json = new JSONDataObject
    json.put("data", "test")

    "instantiate" >> {
      report = new LogReport(json)

      report.isEmpty === false
      report.toString === """{"data":"test"}"""
    }
  }
}