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
package com.crashnote.core.test.unit.collect

import scala.collection.JavaConversions._
import com.crashnote.core.test.defs.MockSpec
import com.crashnote.core.build.Builder
import com.crashnote.core.collect.impl._
import com.crashnote.core.util.SystemUtil
import com.crashnote.core.model.data.DataObject
import com.crashnote.core.test.defs.stubs.ConfigStub

class EnvCollectorSpec
    extends MockSpec[EnvCollector[ConfigStub]] {

    var m_sysUtil: SystemUtil = _

    "Env Collector" should {

        "collect" >> new Mocked() {

            val res = target.collect()
            res.get("started") === 123456789L

            val app = res.get("app").asInstanceOf[DataObject]
            app.get("language") === "java"
            app.get("version") === "1.0"
            app.get("mode") === "dev"
            app.get("client") === "cn:1.0"

            val rt = res.get("runtime").asInstanceOf[DataObject]
            rt.get("type") === "jvm"
            rt.get("name") === "Java 1.6.0"
            rt.get("version") === "1.6.0"
            val rtProps = rt.get("props").asInstanceOf[DataObject]
            rtProps.get("prop1") === "value1"
            rtProps.get("prop2") === "value2"

            val sys = res.get("system").asInstanceOf[DataObject]
            sys.get("id") === 1984
            sys.get("ip") === "192.168.0.1"
            sys.get("name") === "My-PC"
            sys.get("timezone") === "Berlin"
            sys.get("timezone_offset") === 60
            val sysProps = sys.get("props").asInstanceOf[DataObject]
            sysProps.size() === 2
            sysProps.get("instance", "small")
            sysProps.get("secret", "#")
            sys.get("os_name") === "Windows"
            sys.get("os_version") === "6.5"

            val dev = res.get("device").asInstanceOf[DataObject]
            dev.get("cores") === 2
            //dev.get("ram") === 128
            //dev.get("ram_free") === 64
        }
    }

    def configure(config: C) = {
        config.getAppMode returns "dev"
        config.getStartTime returns 123456789L
        config.getAppVersion returns "1.0"
        config.getClientInfo returns "cn:1.0"
        config.getEnvironmentFilters returns Array("secret")

        config.getBuilder returns new Builder
        new EnvCollector[C](config)
    }

    override def mock() {
        m_sysUtil = _mock[SystemUtil]
        m_sysUtil.getSystemId returns 1984
        m_sysUtil.getHostName returns "My-PC"
        m_sysUtil.getTimezoneId returns "Berlin"
        m_sysUtil.getLanguage returns "en-US"
        m_sysUtil.getTimezoneOffset returns 60
        m_sysUtil.getHostAddress returns "192.168.0.1"
        m_sysUtil.getRuntimeName returns "Java 1.6.0"
        m_sysUtil.getRuntimeVersion returns "1.6.0"
        m_sysUtil.getEnvKeys returns new java.util.HashSet(List("instance", "secret", "java.version"))
        m_sysUtil.getEnv("instance") returns ("small")
        m_sysUtil.getEnv("secret") returns ("the earth is flat")
        m_sysUtil.getPropertyKeys returns new java.util.HashSet[AnyRef](List("prop1", "prop2"))
        m_sysUtil.getProperty("prop1") returns ("value1")
        m_sysUtil.getProperty("prop2") returns ("value2")
        m_sysUtil.getAvailableProcessors returns 2
        m_sysUtil.getTotalMemorySize returns 128
        m_sysUtil.getAvailableMemorySize returns 64
        m_sysUtil.getOSName returns "Windows"
        m_sysUtil.getOSVersion returns "6.5"
    }
}