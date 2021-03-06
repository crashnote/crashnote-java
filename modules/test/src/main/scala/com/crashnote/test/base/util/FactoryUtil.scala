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
package com.crashnote.test.base.util

import java.util.Properties
import scala.collection.JavaConversions._

trait FactoryUtil {

    type javaEnum[T] = java.util.Enumeration[T]

    def toEnum[T](seq: Seq[T]): java.util.Enumeration[T] =
        seq.iterator

    def toProps(l: List[(String, Any)], fn: ((String, Any)) => (String, Any)): Properties = {
        val p = new Properties()
        l.map(fn(_)).foreach(kv => p.setProperty(kv._1, kv._2.toString))
        p
    }

    def toProps(l: List[(String, Any)]): Properties =
        toProps(l, tp => tp)

    def toConfProps(l: List[(String, Any)]): Properties =
        toProps(l, tp => ("crashnote." + tp._1, tp._2))
}