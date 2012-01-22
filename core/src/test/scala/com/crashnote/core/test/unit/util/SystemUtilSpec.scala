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
package com.crashnote.core.test.unit.util

import com.crashnote.core.util._
import com.crashnote.test.defs.UnitSpec

class SystemUtilSpec extends UnitSpec {

    "System Util" should {

        val util = new SystemUtil()

        "access global props" >> {
            "find out if a property exists" >> {
                System.setProperty("i-do-exist", "groovy!")

                util.hasProperty("i-do-not-exist") === false
                util.hasProperty("i-do-exist") === true

                val keys = util.getPropertyKeys
                keys must not be empty
            }
        }

        "access env props" >> {
            val keys = util.getEnvKeys
            if (!keys.isEmpty) util.getEnv(keys.iterator().next())
            keys !== null
        }

        "access network props" >> {
            util.getSystemId must not be empty
            util.getHostName must not be empty
            util.getHostAddress must not be empty
        }

        "access runtime props" >> {
            util.getRuntimeName must not be empty
            util.getRuntimeVersion must not be empty
        }

        "access hardware props" >> {
            util.getAvailableProcessors must be_>=(0)
            util.getAvailableMemorySize.toLong must be_>=(0L)
            util.getTotalMemorySize.toLong must be_>=(0L)
        }

        "access locale props" >> {
            util.getLanguage must not be empty
            util.getTimezoneId must not be empty
            util.getTimezoneOffset must not be (null)
        }

        "access operating system props" >> {
            util.getOSName must not be empty
            util.getOSVersion must not be empty
        }

        "load properties file from classpath" >> {
            val props = util.loadProperties("test.properties")
            props.size() === 1
            props.get("marco") === "polo"
        }
    }
}