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
package com.crashnote.logger.test.defs

import com.crashnote.logger.report.LoggerReporter
import com.crashnote.core.model.types.LogLevel
import stubs._

trait AppenderEnv[A, E] {

    self: MockSpec[A] =>

    var m_conf: C = _
    var m_evt: E = _
    var m_reporter: LoggerReporter[C] = _
    var m_confFactory: ConfigFactoryStub = _
    var err = new RuntimeException("oops")

    def create(factory: () => A) {
        target = factory.apply()
    }

    def configure(config: C) = {
        m_reporter = mock[LoggerReporter[C]]
        m_reporter.doAcceptLog(anyString) returns true

        m_conf = mock[ConfigStub]
        m_conf.getLogLevel returns LogLevel.ERROR
        m_conf.getReporter returns m_reporter

        m_confFactory = mock[ConfigFactoryStub]
        m_confFactory.get() returns m_conf

        null
    }
}