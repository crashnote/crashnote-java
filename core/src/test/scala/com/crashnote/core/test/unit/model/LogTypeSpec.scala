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
package com.crashnote.core.test.unit.model

import com.crashnote.core.model.types.LogType
import com.crashnote.test.defs._

class LogTypeSpec
    extends UnitSpec {

    "Log Type" should {

        val vals = LogType.values().toList

        "be created based on file name" >> {
            LogType.ENV == LogType.createFromFileName(LogType.ENV.getExt)
            LogType.ERR == LogType.createFromFileName(LogType.ERR.getExt)
        }

        "signal if has env data" >> {
            LogType.ENV.hasEnvData === true
            LogType.ERR.hasEnvData === false
        }

        "all" >> {
            "have a correctly formatted file extension" >> {
                ((_: String) must startWith(".")).foreach(vals.map(_.getExt))
            }

            "have a file extension ending with '.log'" >> {
                ((_: String) must endWith(".log")).foreach(vals.map(_.getExt))
            }

            "have unique parameters" >> {
                vals.map(_.getExt).distinct.length === vals.length
                vals.map(_.getName).distinct.length === vals.length
                vals.map(_.getCode).distinct.length === vals.length
            }
        }
    }
}