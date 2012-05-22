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
package com.crashnote.core.config;

import com.crashnote.core.config.helper.Config;
import com.crashnote.core.config.helper.ConfigFactory;

import java.util.Properties;

public class CrashConfigFactory<C extends CrashConfig> {

    // VARS =======================================================================================

    protected C config;


    // SETUP =====================================================================================

    public CrashConfigFactory() {
    }


    // INTERFACE ================================================================================

    public final C get() {
        if (config == null) {
            config = create();
            config.validate(getConfFile("crashnote.default"));
        }
        return config;
    }


    // SHARED ===================================================================================

    protected C create() {
        return (C) new CrashConfig(readConf());
    }

    protected Config readConf() {
        return getSysDefault()
                .withFallback(getConfFile("crashnote.about")) // about props
                .withFallback(getConfFile("crashnote")) // user-defined props
                .withFallback(getConfFile("crashnote.default")); // default props

    }

    protected Config getSysDefault() {
        return ConfigFactory.systemProperties() // sys props
                .withFallback(ConfigFactory.systemEnvironment()); // env props
    }

    protected Config getConfProps(final Properties props) {
        return ConfigFactory.parseProperties(props);
    }

    protected Config getConfFile(final String name) {
        return ConfigFactory.load(name);
    }

    protected Config getConfStr(final String str) {
        return ConfigFactory.parseString(str);
    }
}
