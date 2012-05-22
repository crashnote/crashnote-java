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

import com.crashnote.test.defs.UnitSpec
import com.crashnote.core.util.FilterUtil

import scala.collection.JavaConversions._

class FilterUtilSpec extends UnitSpec {

    "Filter Util" should {

        import FilterUtil._

        "filter" >> {
            doFilter("test", List("test")) === true
            doFilter("TEST", List("test")) === true
            doFilter("test", List("t", "test")) === true

            doFilter("test", List("t", "e", "s", "t")) === false
            doFilter("testing", List("test")) === false
            doFilter(" test", List("test")) === false

            doFilter(" Test", List(".*test")) === true
            doFilter("this is a test", List("test", ".*test.*")) === true
            doFilter("test or not to test", List("testing", ".*test.*")) === true
        }
    }
}