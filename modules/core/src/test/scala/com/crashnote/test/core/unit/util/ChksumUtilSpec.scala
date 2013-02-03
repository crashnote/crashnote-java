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
package com.crashnote.test.core.unit.util

import com.crashnote.test.base.defs.UnitSpec
import com.crashnote.core.util.ChksumUtil

class ChksumUtilSpec
  extends UnitSpec {

  import ChksumUtil._

  "Checksum Util" should {

    val data = "{:}ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890?!§$%&/()=?"

    "instantiate" >> {
      new ChksumUtil !== null
    }

    "produce same hash for same inputs" >> {
      var h1 = hash(data)
      var h2 = hash(data)
      h1 === h2

      h1 = hash(data.reverse)
      h2 = hash(data.reverse)
      h1 === h2

      h1 = hash(data.toLowerCase)
      h2 = hash(data.toLowerCase)
      h1 === h2
    }

    "produce different hash for different inputs" >> {
      var h1 = hash(data)
      var h2 = hash(data.reverse)
      h1 !== h2

      h1 = hash(data)
      h2 = hash(data.toLowerCase)
      h1 !== h2
    }
  }
}