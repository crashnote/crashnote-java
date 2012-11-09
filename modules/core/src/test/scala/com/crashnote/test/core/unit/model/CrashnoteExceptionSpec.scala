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
package com.crashnote.test.core.unit.model

import com.crashnote.test.base.defs._
import com.crashnote.core.model.excp.CrashnoteException

class CrashnoteExceptionSpec
    extends UnitSpec {

    "Crashnote Exception" should {

        "have type Throwable" >> {
            new CrashnoteException() must haveSuperclass[Throwable]
        }

        "have default constructor" >> {
            val e = new CrashnoteException()

            e.getMessage must beNull
            e.getCause must beNull
        }

        "have constructor for String" >> {
            val e = new CrashnoteException("oops")

            e.getMessage === "oops"
            e.getCause must beNull
        }

        "have constructor for Throwable" >> {
            val e = new CrashnoteException(th)

            e.getMessage must not(beNull)
            e.getCause === th
        }

        "have constructor for String and Throwable" >> {
            val e = new CrashnoteException("oops", th)

            e.getMessage === "oops"
            e.getCause === th
        }
    }

    val th = new RuntimeException("oops")
}