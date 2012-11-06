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
//package com.crashnote.test.logger.unit.helper
//
//import com.crashnote.test.logger.defs.MockSpec
//import com.crashnote.logger.report.LoggerReporter
//import com.crashnote.logger.helper._
//import com.crashnote.ICrashAppender
//
//class AutoLogConnectorSpec
//    extends MockSpec[AutoLogConnector] {
//
//    var m_conf: C = _
//    var m_reporter: LoggerReporter[C] = _
//
//    "Auto Log Connector" should {
//
//        "instantiate" >> {
//            val c = init(Array())
//            c.getConnectors.size() === 0
//
//            todo
//        }
//    }
//
//    def configure(config: C) = null
//
//    def init(cs: Array[Class[LogConnector[C, ICrashAppender]]]) = {
//        m_conf = mock[C]
//        m_reporter = mock[LoggerReporter[C]]
//
//        new AutoLogConnector(m_conf, m_reporter) {
//            //override protected def getConnectorSources = cs
//        }
//    }
//}