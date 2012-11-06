/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
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

import com.crashnote.external.config.Config;
import com.crashnote.external.config.ConfigFactory;
import com.crashnote.external.config.ConfigParseOptions;

import java.util.Properties;

/**
 * Factory to create instance(s) of {@link CrashConfig}.
 *
 * @param <C> type of the config
 */
public class CrashConfigFactory<C extends CrashConfig> {

    // VARS =======================================================================================

    /**
     * cached config instance
     */
    private C config;


    // SETUP =====================================================================================

    public CrashConfigFactory() {
        // nothing to do
    }


    // INTERFACE ================================================================================

    /**
     * Return an instance of {@link CrashConfig}
     */
    public C get() {

        // use cached version if available
        if (config == null) {

            // 1) create it
            config = create();

            // 2) print it (in debug mode)
            if (config.isDebug()) config.print();

            // 3) validate it (against config schema)
            config.validate(getConfFile("crashnote.default"));
        }

        return config;
    }


    // SHARED ===================================================================================

    /**
     * Create a new configuration by reading properties and files (see below)
     */
    protected C create() {
        return (C) new CrashConfig(readConf());
    }

    /**
     * Create a configuration based on
     * 1) system properties
     * 2) environment properties
     * 3) file 'crashnote.about.conf'
     * 4) file 'crashnote.conf'
     * 5) file 'crashnote.default.conf'
     */
    protected Config readConf() {
        return getSysDefault()
            .withFallback(getConfFile("crashnote.about"))    // about props
            .withFallback(getConfFile("crashnote"))          // user-defined props
            .withFallback(getConfFile("crashnote.default")); // default props

    }

    /**
     * Create a configuration based on
     * 1) system properties
     * 2) environment properties
     */
    protected Config getSysDefault() {
        return ConfigFactory
            .systemProperties()                               // sys props
            .withFallback(ConfigFactory.systemEnvironment()); // env props
    }

    /**
     * Create a configuration by parsing properties
     */
    protected final Config getConfProps(final Properties props, final String descr) {
        final ConfigParseOptions opts =  ConfigParseOptions.defaults().setOriginDescription(descr);
        return ConfigFactory.parseProperties(props, opts);
    }

    /**
     * Create a configuration by loading file
     */
    protected final Config getConfFile(final String name) {
        return ConfigFactory.load(name + ".conf");
    }

    /**
     * Create a configuration by parsing a string
     */
    protected final Config getConfStr(final String str) {
        return ConfigFactory.parseString(str);
    }
}
