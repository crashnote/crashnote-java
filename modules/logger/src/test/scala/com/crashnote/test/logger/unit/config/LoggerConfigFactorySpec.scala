/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
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
package com.crashnote.test.logger.unit.config

import org.specs2.specification.BeforeExample

import com.crashnote.logger.config.{LoggerConfig, LoggerConfigFactory}
import com.crashnote.test.base.defs.UnitSpec
import com.crashnote.core.config.CrashConfigFactory

class LoggerConfigFactorySpec
    extends UnitSpec with BeforeExample {

    "Logger Config Factory" should {

        "inherit from Core Config Factory" >> {
            cf must haveSuperclass[CrashConfigFactory[_]]
        }

        "create configuration instance" >> {
            cf.get must haveClass[LoggerConfig]
        }
    }

    // SETUP ======================================================================================

    var cf: LoggerConfigFactory[LoggerConfig] = _

    def before {
        cf = new LoggerConfigFactory[LoggerConfig]
    }
}