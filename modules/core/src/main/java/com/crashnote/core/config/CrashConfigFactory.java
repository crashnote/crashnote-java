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

    /**
     * instance to load config settings
     */
    protected final ConfigLoader loader;


    // SETUP ======================================================================================

    public CrashConfigFactory() {
        this(new ConfigLoader());
    }

    public CrashConfigFactory(final ConfigLoader loader) {
        this.loader = loader;
    }


    // INTERFACE ==================================================================================

    /**
     * return an instance of {@link CrashConfig}
     */
    public C get() {

        // use cached version if available
        if (config == null) {

            // 1) create it
            config = create();

            // 2) print it (in debug mode)
            if (config.isDebug()) config.print();

            // 3) validate it (against config schema)
            config.validate(loader.fromFile("crashnote.default"));
        }

        return config;
    }


    // SHARED ===================================================================================

    /**
     * create a new configuration by reading properties and files (see below)
     */
    protected C create() {
        @SuppressWarnings("unchecked")
        final C result = (C) new CrashConfig(readConf());
        return result;
    }

    /**
     * create a configuration based on system and files
     */
    protected final Config readConf() {
        return readSysConf()                      // #1 system props
            .withFallback(readUserFileConf())     // #2 user props
            .withFallback(readDefaultFileConf()); // #3 default props
    }

    /**
     * create a configuration based on user's config
     */
    protected Config readUserFileConf() {
        return loader.fromFile("crashnote");
    }

    /**
     * create a configuration from library's default conf files
     */
    protected Config readDefaultFileConf() {
        return
            loader.fromFile("crashnote.about")                          // #1 about props
                .withFallback(loader.fromFile("crashnote.default"));    // #2 default props
    }

    /**
     * create a configuration based on system configs
     */
    protected Config readSysConf() {
        return
            loader.fromSystemProps()                    // #1 sys props
                .withFallback(loader.fromEnvProps());   // #2 env props
    }
}
