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
package com.crashnote.appengine.config;

import com.crashnote.appengine.util.AppengineUtil;
import com.crashnote.core.config.ConfigLoader;
import com.crashnote.external.config.Config;
import com.crashnote.servlet.config.ServletConfigFactory;

import javax.servlet.FilterConfig;

public class AppengineConfigFactory
    extends ServletConfigFactory<AppengineConfig> {

    // SETUP ======================================================================================

    public AppengineConfigFactory(final FilterConfig filterConfig) {
        super(filterConfig);
    }

    public AppengineConfigFactory(final FilterConfig filterConfig, final ConfigLoader loader) {
        super(filterConfig, loader);
    }


    // SHARED =====================================================================================

    @Override
    public AppengineConfig create() {
        return new AppengineConfig(readConf());
    }

    @Override
    protected Config readDefaultFileConf() {

        // create dynamic config file .. (ONLY enable client if running on AppEngine, local requests can just be passed through then)
        final String enabled =
            new AppengineUtil().isRunningOnAppengine() ? "true" : "false";

        final Config appengineConf = loader.fromString(
            "crashnote { enabled = " + enabled + ", request { ignore-localhost = false } }");

        // .. and add it to the application conf
        return appengineConf                            // #1 dynamic AppEngine props (above)
            .withFallback(super.readDefaultFileConf()); // #2 other default props
    }

}
