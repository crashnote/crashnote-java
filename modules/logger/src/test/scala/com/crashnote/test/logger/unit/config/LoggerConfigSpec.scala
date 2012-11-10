/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
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
package com.crashnote.test.logger.unit.config

import com.crashnote.test.base.defs.MockSpec
import org.specs2.specification.BeforeExample
import com.crashnote.logger.config.{LoggerConfig, LoggerConfigFactory}
import com.crashnote.logger.report.LoggerReporter
import com.crashnote.logger.helper.AutoLogConnector
import com.crashnote.core.config.CrashConfig

class LoggerConfigSpec
    extends MockSpec with BeforeExample {

    "Logger Config" should {

        "inherit from Crash Config" >> {
            c must haveSuperclass[CrashConfig]
        }

        "act as factory" >> {
            "for reporter" >> {
                c.getReporter must haveClass[LoggerReporter]
            }
            "for log connector" >> {
                c.getLogConnector(c.getReporter) must haveClass[AutoLogConnector]
            }
        }
    }

    // SETUP =====================================================================================

    var c: LoggerConfig = _

    def before {
        c = (new LoggerConfigFactory[LoggerConfig]).get
    }
}