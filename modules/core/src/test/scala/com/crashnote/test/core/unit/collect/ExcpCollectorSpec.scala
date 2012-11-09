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
package com.crashnote.test.core.unit.collect

import com.crashnote.core.collect.impl.ExcpCollector
import com.crashnote.core.config.CrashConfig
import com.crashnote.core.build.Builder
import com.crashnote.core.model.data._
import java.lang.StackTraceElement
import com.crashnote.test.core.defs.TargetMockSpec

class ExcpCollectorSpec
    extends TargetMockSpec[ExcpCollector] {

    "Excp Collector" should {

        "collect throwable" >> {
            "that is valid" >> new Mock() {

                val th1 = mock[java.lang.Throwable]
                th1.getMessage returns "jdbc user missing"
                th1.getStackTrace returns Array(
                    new StackTraceElement("com.example.Database", "connect", "Database.java", 10),
                    new StackTraceElement("com.example.Database", "connectViaJDBC", "Database.java", 20))

                val th2 = mock[Throwable]
                th2.getMessage returns "wrong argument; nested exception is jdbc user missing"
                th2.getCause returns th1
                th2.getStackTrace returns Array(
                    new StackTraceElement("com.example.DAO", "loadUser", "DAO.java", 30),
                    new StackTraceElement("com.example.DAO", "loadUserByMail", "DAO.java", 40))

                val th3 = mock[Throwable]
                th3.getMessage returns "oops"
                th3.getCause returns th2
                th3.getStackTrace returns Array(
                    new StackTraceElement("com.example.Login", "login", "Login.java", 50),
                    new StackTraceElement("com.example.LoginController", "login", "LoginController.java", 60))

                val res = target.collect(th3)
                res.size() === 3

                val res1 = res.get(0).asInstanceOf[DataObject]
                res1.get("message") === "oops"
                res1.get("class").toString must contain("Throwable")
                val trace1 = res1.get("trace").asInstanceOf[DataArray]
                trace1.get(0) === "com.example.Login:Login.java:login:50"
                trace1.get(1) === "com.example.LoginController:LoginController.java:login:60"

                val res2 = res.get(1).asInstanceOf[DataObject]
                res2.get("message") === "wrong argument"
                res2.get("class").toString must contain("Throwable")
                val trace2 = res2.get("trace").asInstanceOf[DataArray]
                trace2.get(0) === "com.example.DAO:DAO.java:loadUser:30"
                trace2.get(1) === "com.example.DAO:DAO.java:loadUserByMail:40"

                val res3 = res.get(2).asInstanceOf[DataObject]
                res3.get("message") === "jdbc user missing"
                res3.get("class").toString must contain("Throwable")
                val trace3 = res3.get("trace").asInstanceOf[DataArray]
                trace3.get(0) === "com.example.Database:Database.java:connect:10"
                trace3.get(1) === "com.example.Database:Database.java:connectViaJDBC:20"
            }

            "that is null" >> new Mock() {
                target.collect(null) === null
            }
        }
    }

    // SETUP ======================================================================================

    def configure(config: C) = {
        config.getBuilder returns new Builder
        new ExcpCollector(config)
    }
}