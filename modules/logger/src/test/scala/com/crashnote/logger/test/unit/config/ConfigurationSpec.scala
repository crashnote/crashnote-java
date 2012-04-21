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
/*
package com.crashnote.specs.unit.config

import com.crashnote.defs.UnitSpec
import com.crashnote.base.model._
import com.crashnote.base.types._
import com.crashnote.android.config._

class ConfigurationSpec extends UnitSpec {

    "CoreConfig" should {

        setSequential()

        "have correct default values" >> {
            val config = getValidConfig
            config.getPort === 80
            config.isAsync === true
            config.getAsyncInterval === 60
            config.isSecure === true
            config.getOptimizationStrategy === OptimizationStrategy.TRAFFIC
            config.getLogStrategy === LogStrategy.LIVE
            config.getExcludeCommonFrames === true
            config.getHost === "api.logbird.com"
            config.isDebug === false
            config.isEnabled === false
        }

        "create correct URLs" >> {

            "by default" >> {
                val config = new CoreConfig("mykey")
                config.getDataUrl(LogType.ENV) === "https://api.logbird.com:443/env/mykey"
                config.getDataUrl(LogType.EXC) === "https://api.logbird.com:443/excp/mykey"
                config.getValidateUrl === "https://api.logbird.com:443/validate/mykey"
            }
            "for debug" >> {
                val config = new CoreConfig("mykey")
                config.setDebug(true)
                config.getDataUrl(LogType.ENV) === "http://api.logbird.com:80/env/mykey?debug"
                config.getDataUrl(LogType.EXC) === "http://api.logbird.com:80/excp/mykey?debug"
                config.getValidateUrl === "http://api.logbird.com:80/validate/mykey"
            }
            "for non-secure" >> {
                val config = new CoreConfig("mykey")
                config.setSecure("n")
                config.getDataUrl(LogType.ENV) === "http://api.logbird.com:80/env/mykey"
                config.getDataUrl(LogType.EXC) === "http://api.logbird.com:80/excp/mykey"
                config.getValidateUrl === "http://api.logbird.com:80/validate/mykey"
            }
        }

        "validate itself" >> {

            "validate key" >> {
                // valid key
                getValidConfig.validate() === true

                // invalid keys
                getConfig("mykey").validate() must throwA[IllegalArgumentException]
                getConfig("").validate() must throwA[IllegalArgumentException]
                getConfig("  ").validate() must throwA[IllegalArgumentException]
                getConfig(null).validate() must throwA[IllegalArgumentException]
                getConfig("0000000-00000-0000-0000-000000000000").validate() must throwA[IllegalArgumentException]
                getConfig("0000000000000000000000000000000").validate() must throwA[IllegalArgumentException]
            }

            "skip when disabled" >> {
                "by system property" >> {
                    val config = getConfig("invalid-key")
                    System.setProperty(BaseConfig.PROP_ENABLE, "n")
                    config.validate() === true
                }
                "by set" >> {
                    val config = getConfig("invalid-key")
                    System.setProperty(BaseConfig.PROP_ENABLE, "")
                    config.setEnabled("n")
                    config.validate() === true
                }
            }
        }

        "allow change to" >> {
            type S = String
            val config = new CoreConfig

            // bool property
            Seq((config.setEnabled(_), config.isEnabled _, "enabled"),
                (config.setSecure(_: S), config.isSecure _, "secure"),
                (config.setAsync(_), config.isAsync _, "async")) map {
                t => "boolean property " + t._3 >> {
                    t._1("y")
                    t._2() === true
                    t._1("n")
                    t._2() === false
                    t._1("true")
                    t._2() === true
                    t._1("no")
                    t._2() === false
                    t._1("yes")
                    t._2() === true
                    t._1("false")
                    t._2() === false
                    t._1("TRUE")
                    t._2() === true
                    t._1("FALSE")
                    t._2() === false
                    t._1("YES")
                    t._2() === true
                }
            }

            // int property
            Seq((config.setPort(_: String), config.getPort _, "port"),
                (config.setSslPort(_: S), config.getSslPort _, "sslPort"),
                (config.setAsyncInterval(_), config.getAsyncInterval _, "asyncInterval")) map {
                t => "integer property " + t._3 >> {
                    t._1("8080")
                    t._2() === 8080
                    t._1("80")
                    t._2() === 80
                    t._1("abc") must throwAn[IllegalArgumentException]
                }
            }

            // string property
            "key" >> {
                config.setKey(" abcd ");
                config.getKey === "abcd"
            }

            // special rules
            "special rules" >> {
                "async interval: consider minimum value" >> {
                    config.setAsyncInterval("5")
                    config.getAsyncInterval === 30
                }
            }
        }
    }

    private def getConfig(key: String) = {
        System.setProperty(BaseConfigFactory.PROP_ENABLE, "y")
        new CoreConfig(key)
    }

    private def getValidConfig =
        getConfig("0000000-00000-0000-0000-000000000000")
}
*/
