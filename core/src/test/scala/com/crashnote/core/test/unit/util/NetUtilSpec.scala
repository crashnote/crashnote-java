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

import com.crashnote.core.util.NetUtil
import com.crashnote.test.defs.UnitSpec

class NetUtilSpec extends UnitSpec {

    "INet Util" should {

        import NetUtil._

        "find computer's host IP" >> {
            getHostAddress !== null
        }

        "find computer's host anme" >> {
            getHostName !== null
        }

        "read the mac address (or fail silently: return null)" >> {
            getMacAddress
            success
        }
    }
}