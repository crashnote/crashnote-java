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
package com.crashnote.test.servlet.util

import javax.servlet.FilterConfig
import java.util.Properties
import com.crashnote.test.base.defs.MockSpec

trait FactoryUtil {

    self: com.crashnote.test.base.util.FactoryUtil with MockSpec =>

    def filterConfDefaultMock(p: Properties = defaultWebProps) = {
        val m_filterConf = mock[FilterConfig]
        m_filterConf.getInitParameterNames.asInstanceOf[javaEnum[Object]] returns p.keys()
        m_filterConf.getInitParameter(anyString) answers (name => p.getProperty(name.toString))
        m_filterConf
    }

    val defaultWebProps =
        toProps(List(
            "request.hash-ip" -> "false",
            "request.exclude-headers" -> "false",
            "request.exclude-session" -> "true",
            "request.ignore-localhost" -> "false",
            "request.max-parameter-size" -> 1000
        ))
}