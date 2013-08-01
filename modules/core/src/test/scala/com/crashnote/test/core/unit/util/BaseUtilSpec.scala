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
import com.crashnote.core.util.Base64Util

class BaseUtilSpec
  extends UnitSpec {

  import Base64Util._

  "Base64 Util" should {

    "encode" >> {
      new String(encode("This is a test".getBytes)) === "VGhpcyBpcyBhIHRlc3Q="
      new String(encode("This is a test".getBytes)) === new String(encode("This is a test".getBytes))
    }
  }
}