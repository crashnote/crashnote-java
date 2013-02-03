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

import com.crashnote.core.model.types.LogLevel
import com.crashnote.test.base.defs._

class LogLevelSpec
  extends UnitSpec {

  "Log Level" should {

    "convert to String" >> {
      val lvlStrs = LogLevel.values().map(_.toString).toSeq
      "must be unique" >> {
        lvlStrs === lvlStrs.distinct
      }
    }

    "check if is exception" >> {
      LogLevel.TRACE.isExcp === false
      LogLevel.DEBUG.isExcp === false
      LogLevel.INFO.isExcp === false
      LogLevel.ERROR.isExcp === true
      LogLevel.CRASH.isExcp === true
      LogLevel.FATAL.isExcp === true
    }

    "check if level covered" >> {
      val infoLvl = LogLevel.INFO
      infoLvl.covers(LogLevel.TRACE) === false
      infoLvl.covers(LogLevel.DEBUG) === false
      infoLvl.covers(LogLevel.INFO) === true
      infoLvl.covers(LogLevel.ERROR) === true
      infoLvl.covers(LogLevel.FATAL) === true

      val fatalLvl = LogLevel.FATAL
      fatalLvl.covers(LogLevel.INFO) === false
      fatalLvl.covers(LogLevel.ERROR) === false
      fatalLvl.covers(LogLevel.FATAL) === true
    }

    "return max level" >> {
      LogLevel.getMaxLevel(LogLevel.INFO, LogLevel.ERROR) === LogLevel.ERROR
      LogLevel.getMaxLevel(LogLevel.ERROR, LogLevel.INFO) === LogLevel.ERROR
      LogLevel.getMaxLevel(LogLevel.ERROR, LogLevel.ERROR) === LogLevel.ERROR
      LogLevel.getMaxLevel(LogLevel.FATAL, LogLevel.ERROR) === LogLevel.FATAL
    }

    "return min level" >> {
      LogLevel.getMinLevel(LogLevel.INFO, LogLevel.ERROR) === LogLevel.INFO
      LogLevel.getMinLevel(LogLevel.ERROR, LogLevel.INFO) === LogLevel.INFO
      LogLevel.getMinLevel(LogLevel.ERROR, LogLevel.ERROR) === LogLevel.ERROR
      LogLevel.getMinLevel(LogLevel.FATAL, LogLevel.ERROR) === LogLevel.ERROR
    }
  }
}