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
package com.crashnote.test.core.unit.build

import com.crashnote.test.base.defs.UnitSpec
import com.crashnote.core.build.impl.JSONDataArray

class JSONDataArraySpec
    extends UnitSpec {

    "JSON Data Array" should {

        "add" >> {

            val arr = new JSONDataArray

            "objects" >> {
                "not when null" >> {
                    val b = arr.add(null)

                    arr.isEmpty === true
                    b === false
                }

                "when not null" >> {
                    val b = arr.add("test")

                    arr.isEmpty === false
                    b === true
                }
            }

            "array" >> {
                val da = new JSONDataArray

                "not when null" >> {
                    var b = da.addAll(null)

                    da.isEmpty === true
                    b === false
                }

                "not when empty" >> {
                    val b = da.addAll(new JSONDataArray)

                    da.isEmpty === true
                    b === false
                }

                "when not empty and not null" >> {
                    val arr2 = new JSONDataArray
                    arr2.add("test")
                    val b = da.add(arr2)

                    da.isEmpty === false
                    b === true
                }
            }
        }
    }

}