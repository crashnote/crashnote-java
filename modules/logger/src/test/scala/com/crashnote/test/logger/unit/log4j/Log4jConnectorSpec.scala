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
package com.crashnote.test.logger.unit.log4j

import scala.collection.JavaConversions._
import com.crashnote.log4j.impl.Log4jEvt
import com.crashnote.core.model.types.LogLevel
import org.apache.log4j._
import spi._
import com.crashnote.test.base.defs.BaseMockSpec

class Log4jConnectorSpec
    extends BaseMockSpec[LoggingEvent] {

    "Log4j Connector" should {

        "" >> {
            1 === 1 // TODO
        }
    }
}