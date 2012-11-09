/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crashnote.test.core.unit.config

import com.crashnote.test.base.defs.UnitSpec
import com.crashnote.core.config.ConfigLoader
import java.util.Properties

class ConfigLoaderSpec
    extends UnitSpec {

    "Config Loader" should {

        val l = new ConfigLoader()

        "load from file" >> {
            val c = l.fromFile("test")

            c.getString(path) === "42"
        }

        /*
        "load from system" >> {
            System.setProperty(path, "42")
            val c = l.fromSystemProps()

            c.getString(path) === "42"
        }
        */

        "load from props" >> {
            val p = new Properties()
            p.setProperty(path, "42")
            val c = l.fromProps(p, "props")

            c.getString(path) === "42"
        }

        "load from String" >> {
            val c = l.fromString(
                """crashnote {
                   answer = 42
                }"""
            )

            c.getString(path) === "42"
            1 === 1
        }
    }

    val path = "crashnote.answer"
}