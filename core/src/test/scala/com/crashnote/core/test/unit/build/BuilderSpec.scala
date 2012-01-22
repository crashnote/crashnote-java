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
package com.crashnote.core.test.unit.build

import com.crashnote.test.defs.UnitSpec
import com.crashnote.core.build.Builder
import com.crashnote.core.model.data.{DataArray, DataObject}

class BuilderSpec
    extends UnitSpec {

    "Builder" should {

        val b = new Builder

        "create empty data object" >> {
            val obj = b.createDataObj()

            obj !== null
            obj.isEmpty === true
            obj must haveInterface[DataObject]
        }

        "create empty data array" >> {
            val arr = b.createDataArr()

            arr !== null
            arr.isEmpty === true
            arr must haveInterface[DataArray]
        }
    }

}