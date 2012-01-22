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
package com.crashnote.core.test.unit.send

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

import java.net.HttpURLConnection
import com.crashnote.core.test.defs.MockSpec
import com.crashnote.core.config.Config
import com.crashnote.core.model.types.LogType
import com.crashnote.core.model.log.LogReport
import com.crashnote.core.build.impl.JSONDataObject
import java.io.{Writer, OutputStream, IOException}
import com.crashnote.core.send.Sender
import com.crashnote.core.test.defs.stubs.ConfigStub

class SenderSpec
    extends MockSpec[Sender[ConfigStub]] {

    var report: LogReport = _
    var m_conn: HttpURLConnection = _
    var m_stream: OutputStream = _
    var m_writer: Writer = _

    "Sender" should {

        "send" >> {
            "with success" >> new Response(200) {
                target.send(report)
                checkConnection(url, "POST")
            }
            "with stream" >> {
                "write error" >> new Response(0) {
                    m_stream.write(any[Array[Byte]]) throws new IOException("oops")
                    target.send(report) === false
                    checkConnection(url, "POST")
                }
                "flush error" >> new Response(0) {
                    m_stream.flush() throws new IOException("oops")
                    target.send(report) === false
                    checkConnection(url, "POST")
                }
                "close error" >> new Response(0) {
                    m_stream.close() throws new IOException("oops")
                    target.send(report)
                    checkConnection(url, "POST")
                }
            }
            "with writer" >> {
                "write error" >> new Response(0) {
                    m_writer.write(anyString) throws new IOException("oops")
                    target.send(report) === false
                    checkConnection(url, "POST")
                }
                "flush error" >> new Response(0) {
                    m_writer.flush() throws new IOException("oops")
                    target.send(report) === false
                    checkConnection(url, "POST")
                }
                "close error" >> new Response(0) {
                    m_writer.close() throws new IOException("oops")
                    target.send(report)
                    checkConnection(url, "POST")
                }
            }
            "with connect error" >> new Response(-1) {
                target.send(report) === false
            }
        }
    }

    def checkConnection(url: String, typeOf: String = "GET") =
        if (m_conn != null) {
            m_conn.getURL.toURI.toString === url
            if (typeOf == "POST") {
                expect {
                    one(m_conn).setRequestProperty("Accept", "application/x-gzip");
                    one(m_conn).setRequestProperty("Content-Type", "application/json");
                    one(m_conn).setRequestProperty("Content-Encoding", "gzip");
                    one(m_writer).close()
                    one(m_stream).close()
                }
            }
            expect {
                one(m_conn).setRequestMethod(typeOf)
                one(m_conn).setUseCaches(false)
                one(m_conn).setDoOutput(true)
                one(m_conn).setConnectTimeout(10000)
                one(m_conn).setReadTimeout(10000)
                one(m_conn).disconnect()
            }
        }

    val key = "00000000-0000-0000-0000-00000000000"
    val url = "https://api.crashnote.com:443/err"
    val client = "spec-1.0"

    override def mockConfig() = {
        val mc = super.mockConfig()
        mc.getKey returns key
        mc.getPostUrl returns url
        mc.getClientInfo returns client
        mc.getConnectionTimeout returns 10
        mc
    }

    class Response(resp: java.lang.Integer, repType: LogType = LogType.ERR) extends Configured {

        val m_conf = mockConfig()
        configure(m_conf)

        target = new Sender[C](m_conf) {
            override protected def createConnection(url: String) = {
                m_conn = null
                m_stream = null

                if (resp == -1) throw new IOException("oops")

                // init connection
                m_conn = spy[HttpURLConnection](super.createConnection(url))
                if (resp == 0)
                    doThrow(new IOException("oops")).when(m_conn).getResponseCode
                else
                    doReturn(resp).when(m_conn).getResponseCode

                // init stream
                m_stream = mock[OutputStream]
                doReturn(m_stream).when(m_conn).getOutputStream

                m_conn
            }

            override protected def createWriter(stream: OutputStream) = {
                m_writer = mock[Writer]
                m_writer
            }
        }

        report = new LogReport(new JSONDataObject)
    }

    def configure(config: C) = null
}