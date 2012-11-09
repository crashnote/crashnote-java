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
package com.crashnote.test.logger.defs

import org.specs2.specification.Scope
import com.crashnote.core.Lifecycle
import com.crashnote.test.core.defs.BaseMockSpec

abstract class TargetMockSpec[T](implicit t: Manifest[T])
    extends BaseMockSpec[T] with LoggerEnv {

    // ==== CONTEXTS

    // stage #1: config the target
    def configure(config: C): T

    class Configured(fns: (C) => _*) extends Scope {

        doSetup()

        def doSetup() {
            val m_conf = mockConfig()
            fns.foreach(fn => fn.apply(m_conf))
            target = configure(m_conf)
        }
    }

    // stage #2: mock the target's components
    def mock() {}

    class Mock(fns: (C) => _*) extends Configured(fns: _*) {

        mock()
    }

    // stage #3: start the target
    def start() {
        if (target.isInstanceOf[Lifecycle])
            target.asInstanceOf[Lifecycle].start()
        afterStarted()
    }

    def afterStarted() = {}

    class Started(fns: (C) => _*) extends Mock(fns: _*) {

        start()
    }

    // ==== CONFIGS

    lazy val DISABLED = (config: C) => config.isEnabled returns false
    lazy val ENABLED = (config: C) => config.isEnabled returns true
}