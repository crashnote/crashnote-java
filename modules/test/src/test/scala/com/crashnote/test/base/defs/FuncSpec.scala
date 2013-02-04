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

import org.eclipse.jetty.server.{Request, Server}
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.specs2.specification.{Fragments, Step}
import collection.mutable
import org.apache.commons.io.IOUtils

trait FuncSpec
  extends UnitSpec {

  var server: Server = null
  val serverPort = nextFreePort()


  // SETUP =====================================================================================

  override def map(fs: => Fragments) =
    Step(startServer()) ^ Step(executeRequest()) ^ Step(stopServer()) ^ fs

  def startServer() {
    println(s"CREATING JETTY ($serverPort)")
    server = new Server(serverPort)
    server.setHandler(new AbstractHandler() {
      override def doStart() {
        println("STARTING JETTY")
        super.doStart()
      }

      override def doStop() {
        println("STOPPING JETTY")
        super.doStop()
      }

      def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        handleReq(target, baseRequest, request, response)
      }
    })
    server.start()
  }

  def executeRequest()

  def stopServer() {
    if (server != null)
      server.stop()
  }


  // SHARED ====================================================================================

  var body: String = null
  var query: String = null
  var headers: mutable.Map[String, String] = null

  protected def handleReq(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    import collection.JavaConverters._

    headers = mutable.Map[String, String]()
    for (hn <- baseRequest.getHeaderNames.asScala) {
      headers += (hn.toString -> baseRequest.getHeader(hn.toString))
    }

    query = baseRequest.getQueryString

    val bytes = new Array[Char](request.getContentLength)
    IOUtils.read(baseRequest.getReader, bytes)
    body = new String(bytes)
  }


  // INTERNALS =================================================================================

  private def nextFreePort() = {
    val s = new java.net.ServerSocket(0)
    val p = s.getLocalPort
    s.close()
    p
  }
}