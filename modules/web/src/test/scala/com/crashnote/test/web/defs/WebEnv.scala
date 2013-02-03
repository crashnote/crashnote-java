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
package com.crashnote.test.web.defs

import com.crashnote.core.log.LogLogFactory
import com.crashnote.test.base.defs.BaseMockSpec
import com.crashnote.web.config.WebConfig

trait WebEnv {

  self: BaseMockSpec[_] =>


  type C = WebConfig

  var m_conf = mock[WebConfig]

  def mockConfig(): C = {
    val lfact = new LogLogFactory(m_conf)
    m_conf.getLogger(anyClass) returns lfact.getLogger("")
    m_conf.getLogger(anyString) returns lfact.getLogger("")
    m_conf
  }
}