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
//package com.crashnote.test.servlet.defs
//
//import org.eclipse.jetty.server.Server
//import org.eclipse.jetty.webapp.WebAppContext
//import org.specs2.specification._
//import akka.actor._
//import akka.actor.Actor._
//import akka.routing._
//import akka.routing.Routing.Broadcast
//import com.crashnote.test.util.MockUser
//import com.crashnote.test.servlet.defs.FuncSpec
//
//trait ServletSpec
//    extends FuncSpec {
//
//    var servlet: Server = _
//    val servletStopWait = 1000
//    val servletPort = nextFreePort()
//
//    val subResDir = ""
//
//    // SETUP ======================================================================================
//
//    override def map(fs: => Fragments) =
//        Step(startServer()) ^ Step(startServlet()) ^ Step(startClients()) ^ // start
//            Step(execClients()) ^ fs ^ // execute
//            Step(stopServlet()) ^ Step(stopServer()) ^ Step(stopClients()) // shutdown
//
//    // ==== SERVLET
//
//    def startServlet() {
//        val context = new WebAppContext()
//        context.setParentLoaderPriority(true)
//
//        context.setContextPath("/")
//        context.setResourceBase("./servlet/target/test-classes/")
//        context.setDescriptor("./servlet/src/test/resources/web.xml")
//
//        System.setProperty("crashnote.port", serverPort.toString)
//        servlet = new Server(servletPort)
//        servlet.setHandler(context)
//
//        servlet.start()
//        while (!servlet.isRunning) {
//            /* wait */
//        }
//        //servlet.join()
//    }
//
//    def stopServlet() {
//        if (servlet != null) {
//            Thread.sleep(servletStopWait)
//            servlet.stop()
//        }
//    }
//
//    // ==== CLIENTS
//
//    def startClients() {
//        router.start()
//        clients foreach (router.startLink(_))
//    }
//
//    def execClients()
//
//    def stopClients() {
//        router ! Broadcast(PoisonPill)
//        router ! PoisonPill
//    }
//
//
//    // INTERFACE ==================================================================================
//
//    def callURL() {
//        router ! "http://127.0.0.1:" + servletPort + "/api/errors"
//    }
//
//    // INTERNALS ==================================================================================
//
//    private lazy val clients =
//        Vector.fill(5)(actorOf[MockUser])
//
//    private val router =
//        Routing.loadBalancerActor(CyclicIterator(clients))
//}