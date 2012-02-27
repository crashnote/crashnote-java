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
package com.crashnote.core.test.unit.config

import com.crashnote.test.defs.UnitSpec
import org.specs2.specification.BeforeExample
import com.crashnote.core.test.defs.stubs._

class ConfigSpec
    extends UnitSpec with BeforeExample {

    var c: ConfigStub = _

    "Config" should {

        val l = new ConfigListenerStub

        "act as factory" >> {
            "for builder" >> {
                c.getBuilder !== null
            }
            "for sender" >> {
                c.getSender !== null
            }
            "for collector" >> {
                c.getCollector !== null
            }
            "for system util" >> {
                c.getSystemUtil !== null
            }
            "for reporter" >> {
                c.getReporter !== null
            }
        }

        "manage config change listeners" >> {
            "add and remove listeners" >> {
                c.addListener(l)
                c.getListeners must have size (1)
                c.removeAllListeners()
                c.getListeners must have size (0)

                c.addListener(l)
                c.addListener(l)
                c.getListeners must have size (1)

                c.removeListener(l)
                c.getListeners must have size (0)
            }
            "notify listeners on change" >> {
                c.addListener(l)
                l.updateCount === 0

                c.updateComponentsConfig()
                l.updateCount === 1

                c.updateComponentsConfig()
                l.updateCount === 2
            }
        }
    }

    def before =
        c = (new ConfigFactoryStub).get
}