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

import org.specs2.mutable.BeforeAfter
import org.eclipse.jetty.server.{Request, Server}
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

trait FuncSpec
  extends UnitSpec with BeforeAfter {

  var server: Server = null
  val serverPort = nextFreePort()


  // SETUP =====================================================================================

  def before {
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

  def after {
    if (server != null)
      server.stop()
  }


  // SHARED ====================================================================================

  protected def handleReq(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse)


  // INTERNALS =================================================================================

  private def nextFreePort() = {
    val s = new java.net.ServerSocket(0)
    val p = s.getLocalPort
    s.close()
    p
  }
}