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
package com.crashnote.servlet.test.defs

import com.crashnote.core.log.LogLog
import com.crashnote.test.defs.BaseMockSpec
import stubs.ConfigStub

trait ServletEnv {

    self: BaseMockSpec[_] =>

    type C = ConfigStub

    def mockConfig(): C = {
        val m_conf = mock[ConfigStub]
        m_conf.getLogger(anyClass) returns new LogLog("")
        m_conf.getLogger(anyString) returns new LogLog("")
        m_conf
    }
}