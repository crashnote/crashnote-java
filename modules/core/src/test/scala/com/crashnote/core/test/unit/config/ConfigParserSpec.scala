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
package com.crashnote.core.test.unit.config

import com.crashnote.test.defs._
import com.crashnote.core.config.ConfigParser

class ConfigParserSpec
    extends UnitSpec {

    setSequential()

    "Config Parser" should {

        val parser = new ConfigParser

        "parse integer" >> {
            parser.parseInt("1") === 1
            parser.parseInt("-1000") === -1000

            "but throw exception if invalid" >> {
                parser.parseInt("a") must throwA[IllegalArgumentException]
            }
        }
        "parse string" >> {
            parser.parseString("test") === "test"
            parser.parseString("TEST") === "test"
            parser.parseString(" test ") === "test"
            parser.parseString(" TEST ") === "test"
            parser.parseString(null) === null
        }
        "parse bool" >> {
            parser.parseBool("true") === true
            parser.parseBool(" true ") === true
            parser.parseBool("yes") === true
            parser.parseBool("  y ") === true
            parser.parseBool("ON") === true
            parser.parseBool(null) === false
        }
    }
}