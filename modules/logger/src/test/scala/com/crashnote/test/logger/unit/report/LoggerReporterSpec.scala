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
package com.crashnote.test.logger.unit.report

import com.crashnote.logger.report.LoggerReporter

import com.crashnote.test.logger.defs.TargetMockSpec

class LoggerReporterSpec
    extends TargetMockSpec[LoggerReporter] {

    "Logger Reporter" should {

        "decide whether to accept logs" >> {

            "when enabled" >> new Mock(ENABLED) {
                target.doAcceptLog("com.example") === true
                target.doAcceptLog("com.crashnote") === false
                target.doAcceptLog("com.crashnote.core") === false
            }
            "when disabled" >> new Mock(DISABLED) {
                target.doAcceptLog("com.example") === false
                target.doAcceptLog("com.crashnote") === false
            }
        }
    }

    // SETUP =====================================================================================

    def configure(config: C) =
        new LoggerReporter(config)
}