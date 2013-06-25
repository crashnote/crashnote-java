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
///**
// * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.crashnote.test.base.util
//
//import spray._
//import can._
//import encoding._
//import http.StatusCode
//
//import akka.actor._
//import Actor._
//import akka.config.Supervision._
//import org.slf4j.LoggerFactory
//
//object MockServer {
//
//    var server: Supervisor = _
//
//    def boot(port: Int = 8888, onReceive: (String, String) => (StatusCode, String)) {
//
//        LoggerFactory.getLogger(getClass)
//
//        // we need an ActorSystem to host our application in
//      val system = ActorSystem("example")
//
//        // the service actor replies to incoming HttpRequests
//        val serviceActor = system.actorOf(Props[TestService])
//
//        // === my service
//        val mainModule = new MockService {
//            def receive(key: String, data: String) = onReceive(key, data)
//        }
//        val restService = actorOf(new HttpService(mainModule.directive))
//
//        // === root service
//        val rootService = actorOf(new SprayCanRootService(restService))
//
//        // === server service
//        val serverConf = ServerConfig.fromAkkaConf.copy(
//            port = port, requestTimeout = 5000
//        )
//        val sprayCanServer = actorOf(new HttpServer(serverConf))
//
//        server = Supervisor(
//            SupervisorConfig(
//                OneForOneStrategy(List(classOf[Exception]), 3, 100),
//                List(
//                    Supervise(restService, Permanent),
//                    Supervise(rootService, Permanent),
//                    Supervise(sprayCanServer, Permanent)
//                )
//            )
//        )
//    }
//
//    def shutdown() {
//        if (server != null) server.shutdown()
//    }
//}
//
//trait MockService extends Directives {
//
//    val directive = {
//        path("err") {
//            path("") {
//                get {
//                    _.complete("Hello")
//                }
//            } ~ post {
//                parameters('key ?) {
//                    (pKey: Option[String]) =>
//                        (decodeRequest(Gzip) | decodeRequest(Deflate) | decodeRequest(NoEncoding)) {
//                            content(as[String]) {
//                                bytes =>
//                                    detach {
//                                        val r = receive("", bytes)
//                                        _.complete(r._1, r._2)
//                                    }
//                            }
//                        }
//                }
//            }
//        }
//    }
//
//    def receive(key: String, data: String): (StatusCode, String)
//}