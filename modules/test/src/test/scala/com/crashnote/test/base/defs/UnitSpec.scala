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
package com.crashnote.test.base.defs

import org.specs2.matcher.DataTables
import org.specs2.mutable._
import com.crashnote.test.base.util.FactoryUtil
import java.io.{OutputStream, PrintStream, ByteArrayOutputStream}
import org.specs2.specification.Example

trait UnitSpec
    extends SpecificationWithJUnit
    with DataTables
    with FactoryUtil
    with PrintCapture {

    sequential
}