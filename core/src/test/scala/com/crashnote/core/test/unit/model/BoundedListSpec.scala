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
package com.crashnote.core.test.unit.model

import com.crashnote.core.model.data.BoundedList
import scala.collection.JavaConversions._
import com.crashnote.test.defs._

class BoundedListSpec
    extends UnitSpec {

    setSequential()

    "Bounded List" should {

        val list = new BoundedList[Int](2)

        "always obey the maximum number of elements" >> {
            "add" >> {
                list.add(2)

                list.toString === "[ 2 ]"
            }
            "add with location" >> {
                list.add(0, 1)

                list.get(0) == 1
                list.get(1) == 2
                list.toString === "[ 1 2 ]"

                list.add(1, 3)
                list.toString === "[ 2 3 ]"
            }
            "add last" >> {
                list.addLast(4)

                list.toString === "[ 3 4 ]"
            }
        }

        "throw exception for unsupported methods" >> {
            "add all" >> {
                list.addAll(Seq(1)) must throwA[UnsupportedOperationException]
                list.addAll(0, Seq(1)) must throwA[UnsupportedOperationException]
            }
            "add first" >> {
                list.addFirst(1) must throwA[UnsupportedOperationException]
            }
        }
    }
}