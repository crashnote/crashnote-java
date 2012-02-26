package com.crashnote.appengine.test.unit.collect

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

import com.crashnote.appengine.util.AppengineUtil
import com.crashnote.appengine.collect.impl.AppengineEnvCollector
import com.crashnote.appengine.test.defs.MockSpec
import com.crashnote.appengine.config.AppengineConfig
import com.crashnote.core.model.data.DataObject
import com.crashnote.core.build.Builder

class AppengineEnvCollectorSpec
    extends MockSpec[AppengineEnvCollector[AppengineConfig]] {

    var m_appengineUtil: AppengineUtil = _

    "AppEngine Env Collector" should {

        "collect" >> new Mocked() {
            m_appengineUtil.getProperty("com.google.appengine.application.id") returns "TEST"
            m_appengineUtil.getProperty("com.google.appengine.application.version") returns "1.0"
            m_appengineUtil.getProperty("com.google.appengine.runtime.environment") returns "Production"
            m_appengineUtil.getProperty("com.google.appengine.runtime.version") returns "1-5a.356739207231993312"

            val res = target.collect()

            val app = res.get("app").asInstanceOf[DataObject]
            app.get("id") === "TEST"
            app.get("version") === "1-5a"
            app.get("build") === "356739207231993312"

            val rt = res.get("runtime").asInstanceOf[DataObject]
            rt.get("code") === "2.0"
        }
    }

    def configure(config: C) = {
        config.getBuilder returns new Builder()
        new AppengineEnvCollector[C](config)
    }

    override def mock {
        m_appengineUtil = _mock[AppengineUtil]
    }
}