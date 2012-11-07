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
//package com.crashnote.test.unit.config
//
//import com.crashnote.appengine.util.AppengineUtil
//import com.crashnote.appengine.config.AppengineConfig
//import com.crashnote.test.defs.TargetMockSpec
//
//class AppengineConfigSpec
//    extends TargetMockSpec[AppengineConfig] {
//
//    var m_appengineUtil: AppengineUtil = _
//
//    "AppEngine Config" should {
//
//        "always be in sync mode" >> {
//            "by default" >> new Mock(SYNC) {
//                target.isSync === true
//            }
//            "even when config is set to async" >> new Mock(ASYNC) {
//                target.isSync === true
//            }
//        }
//    }
//
//    def configure(config: C) = {
//        m_appengineUtil = mock[AppengineUtil]
//        new AppengineConfig() {
//            override def getSystemUtil = m_appengineUtil
//        }
//    }
//
//}