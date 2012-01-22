package com.crashnote.servlet.test.unit.config

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

import org.specs2.specification.BeforeExample
import com.crashnote.servlet.test.defs.stubs.ConfigStub
import com.crashnote.servlet.test.defs.stubs.ConfigFactoryStub
import com.crashnote.test.defs.UnitSpec
import com.crashnote.servlet.report.ServletReporter

class ServletConfigSpec
    extends UnitSpec with BeforeExample {

    var c: ConfigStub = _

    "Servlet Config" should {

        "act as factory" >> {
            "for servlet reporter" >> {
                c.getReporter must haveClass[ServletReporter[ConfigStub]]
            }
        }
    }

    def before =
        c = (new ConfigFactoryStub).get
}