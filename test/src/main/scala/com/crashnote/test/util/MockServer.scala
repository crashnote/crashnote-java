package com.crashnote.test.util

import cc.spray._
import can._
import encoding._
import http.StatusCode

import akka.actor._
import Actor._
import akka.config.Supervision._
import org.slf4j.LoggerFactory

object MockServer {

    var server: Supervisor = _

    def boot(port: Int = 8888, onReceive: (String, String) => (StatusCode, String)) {

        LoggerFactory.getLogger(getClass)

        // === my service
        val mainModule = new MockService {
            def receive(key: String, data: String) = onReceive(key, data)
        }
        val restService = actorOf(new HttpService(mainModule.directive))

        // === root service
        val rootService = actorOf(new SprayCanRootService(restService))

        // === server service
        val serverConf = ServerConfig.fromAkkaConf.copy(
            port = port, requestTimeout = 5000
        )
        val sprayCanServer = actorOf(new HttpServer(serverConf))

        server = Supervisor(
            SupervisorConfig(
                OneForOneStrategy(List(classOf[Exception]), 3, 100),
                List(
                    Supervise(restService, Permanent),
                    Supervise(rootService, Permanent),
                    Supervise(sprayCanServer, Permanent)
                )
            )
        )
    }

    def shutdown() {
        if (server != null) server.shutdown()
    }
}

trait MockService extends Directives {

    val directive = {
        path("err") {
            path("") {
                get {
                    _.complete("Hello")
                }
            } ~ post {
                parameters('key ?) {
                    (pKey: Option[String]) =>
                        (decodeRequest(Gzip) | decodeRequest(Deflate) | decodeRequest(NoEncoding)) {
                            content(as[String]) {
                                bytes =>
                                    detach {
                                        val r = receive("", bytes)
                                        _.complete(r._1, r._2)
                                    }
                            }
                        }
                }
            }
        }
    }

    def receive(key: String, data: String): (StatusCode, String)
}