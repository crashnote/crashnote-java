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
package com.crashnote.test.appengine.unit.log

import com.crashnote.appengine.log.AppengineLogLogFactory
import com.crashnote.appengine.config.AppengineConfig
import com.crashnote.test.appengine.defs.TargetMockSpec

class AppengineLogSpec
    extends TargetMockSpec[AppengineLogLogFactory] {

    "AppEngine LogLog" should {

        "instantiate via factory" >> new Configured {
            target.getLogger("TEST").getName === "TEST"
        }

        "print" >> {
            "debug" >> new Configured {
                target.getLogger("TEST").debug("test: {}", "test")
            }
            "info" >> new Configured {
                target.getLogger("TEST").info("test: {}", "test")
            }
            "warn" >> new Configured {
                "with message" >> {
                    target.getLogger("TEST").warn("test: {}", "test")
                }
                "with exception" >> {
                    target.getLogger("TEST").warn("test", th)
                }
                "with exception and parameters" >> {
                    target.getLogger("TEST").warn("test: {}", th, "test")
                }
            }
            "error" >> new Configured {
                "with message" >> {
                    target.getLogger("TEST").error("test: {}", "test")
                }
                "with exception" >> {
                    target.getLogger("TEST").error("test", th, "test")
                }
            }
        }
    }

    // SETUP ======================================================================================

    def configure(config: AppengineConfig) =
        new AppengineLogLogFactory(config)

    val prefix = "CRASHNOTE"
    val th = new RuntimeException("oops")
}