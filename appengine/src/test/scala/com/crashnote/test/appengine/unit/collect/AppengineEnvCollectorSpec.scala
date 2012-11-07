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
package com.crashnote.test.appengine.unit.collect

import com.crashnote.appengine.util.AppengineUtil
import com.crashnote.appengine.collect.impl.AppengineEnvCollector
import com.crashnote.test.appengine.defs.TargetMockSpec
import com.crashnote.appengine.config.AppengineConfig
import com.crashnote.core.model.data.DataObject
import com.crashnote.core.build.Builder
import java.util.Date

class AppengineEnvCollectorSpec
    extends TargetMockSpec[AppengineEnvCollector[AppengineConfig]] {

    var startTime: Long = new Date().getTime
    var m_appengineUtil: AppengineUtil = _

    "AppEngine Env Collector" should {

        "collect" >> {
            "by default" >> new Mock() {
                val res = target.collect()

                val app = res.get("app").asInstanceOf[DataObject]
                app.get("id") === "TEST"
                app.get("version") === "1-5a"
                app.get("build") === "356739207231993312"

                val rt = res.get("runtime").asInstanceOf[DataObject]
                rt.get("code") === "2.0"
            }

            "but use start time for 'build' when running local" >> new Mock() {
                m_appengineUtil.isRunningOnAppengine returns false
                val res = target.collect()

                val app = res.get("app").asInstanceOf[DataObject]
                app.get("build") === startTime
            }

            "but don't overwrite existing" >> {
                "version" >> new Mock(WITH_VERSION) {
                    val res = target.collect()

                    val app = res.get("app").asInstanceOf[DataObject]
                    app.get("version") === "1.4.2"
                }
                "build" >> new Mock(WITH_BUILD) {
                    val res = target.collect()

                    val app = res.get("app").asInstanceOf[DataObject]
                    app.get("build") === "b42"
                }
            }
        }
    }

    def configure(config: C) =
        new AppengineEnvCollector[C](config)

    override def mockConfig() = {
        val m_conf = super.mockConfig()
        m_conf.getBuilder returns new Builder()
        m_conf.getStartTime returns startTime
        m_conf
    }

    override def mock() {
        m_appengineUtil = _mock[AppengineUtil]

        m_appengineUtil.isRunningOnAppengine returns true

        m_appengineUtil.getProperty("com.google.appengine.application.id") returns "TEST"
        m_appengineUtil.getProperty("com.google.appengine.application.version") returns "1-5a.356739207231993312"

        m_appengineUtil.getProperty("com.google.appengine.runtime.version") returns "2.0"
        m_appengineUtil.getProperty("com.google.appengine.runtime.environment") returns "Production"
    }

    lazy val WITH_BUILD = (config: C) => config.getBuild returns "b42"
    lazy val WITH_VERSION = (config: C) => config.getVersion returns "1.4.2"
}