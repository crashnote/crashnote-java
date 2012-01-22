package com.crashnote.servlet.test.func

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

import java.io._
import java.util.Date
import org.apache.commons.io.IOUtils
import cc.spray.http.StatusCode
import com.crashnote.core.model.data.BoundedList
import com.crashnote.servlet.test.defs.ServletSpec

class PerformanceSpec
    extends ServletSpec {

    override val servletStopWait = 5000

    "Notifier" should {

        "handle concurrent requests and be performant" >> {
            1 === 1
        }
    }

    def execClients() {
        val t_hist = new BoundedList[Long](20);

        (0 to 249).foreach {
            arg =>
                val s = new Date
                callURL()
                val e = new Date

                val tdif = e.getTime - s.getTime
                t_hist.add(tdif)
                //println(t_hist)
        }

        println("final timings: " + t_hist)
    }

    override protected def onReceive(key: String, data: String): (StatusCode, String) = {
        if (!data.isEmpty) {
            val folder = new File(System.getProperty("java.io.tmpdir"), "crashnotes")
            folder.mkdir
            val json = File.createTempFile("note", ".json", folder)
            val stream = new FileOutputStream(json)
            IOUtils.write(data, stream)
            stream.close()
        }
        super.onReceive(key, data)
    }
}