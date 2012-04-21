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
package com.crashnote.core.test.unit.build

import com.crashnote.test.defs.UnitSpec
import com.crashnote.core.build.impl.{JSONDataArray, JSONDataObject}

class JSONDataObjectSpec
    extends UnitSpec {

    setSequential()

    "JSON Data Object" should {

        /*
        "stream to file and back again" >> {
            val util = new FileUtil
            val obj = new JSONDataObject
            obj.put("key", "value")
            val f = File.createTempFile("stream", "json")
            util.writeToFile(f, obj)
            val res = util.readFromFile(f)

            obj === res
        }
        */

        "append data array" >> {
            val obj = new JSONDataObject

            "when array exists" >> {
                val arr = new JSONDataArray
                obj.put("arr", arr)
                obj.appendTo("arr", "test")

                arr.isEmpty === false
            }

            "when array does not exist" >> {
                obj.appendTo("void", "test")

                val arr = obj.get("void")
                arr.asInstanceOf[JSONDataArray].isEmpty === false
            }
        }

        "put" >> {

            "object" >> {
                val obj = new JSONDataObject

                "not when null" >> {
                    obj.put("key", null)
                    obj.isEmpty === true
                }
                "when not null and not empty" >> {
                    obj.put("key", "value")
                    obj.isEmpty === false
                }
            }

            "array" >> {
                val obj = new JSONDataObject

                "not when null" >> {
                    obj.putArr("key", null)
                    obj.isEmpty === true
                }

                "not when empty" >> {
                    val arr = new JSONDataArray
                    obj.putArr("key", arr)
                    obj.isEmpty === true
                }

                "when not null and not empty" >> {
                    val arr = new JSONDataArray
                    arr.add("value")
                    obj.putArr("key", arr)
                    obj.isEmpty === false
                }
            }

            "data object" >> {
                val obj = new JSONDataObject

                "not when null" >> {
                    obj.putObj("key", null)
                    obj.isEmpty === true
                }

                "not when empty" >> {
                    val obj = new JSONDataObject
                    obj.putObj("key", obj)
                    obj.isEmpty === true
                }

                "when not null and not empty" >> {
                    val obj2 = new JSONDataObject
                    obj2.put("key", "value")
                    obj.putObj("key", obj2)
                    obj.isEmpty === false
                }
            }

            "map" >> {
                val obj = new JSONDataObject

                "not when null" >> {
                    obj.putObj("key", null)
                    obj.isEmpty === true
                }

                "when not null and not empty" >> {
                    val obj2 = new JSONDataObject
                    obj2.put("key", "value")
                    obj.putObj("key", obj2)
                    obj.isEmpty === false
                }
            }
        }

        /*
        "be deep copyable" >> {
            val obj = new JSONDataObject

            val obj2 = new JSONDataObject
            obj2.put("key", "value")
            obj.put("obj", obj2)

            val arr = new JSONDataArray
            arr.add("value")
            obj.put("arr", arr)

            val copy = new JSONDataObject(obj)

            obj must not beTheSameAs (copy)
            obj.get("arr") must not beTheSameAs (arr)
            obj.get("obj") must not beTheSameAs (obj2)
        }
        */
    }

}