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
package com.crashnote.web.config;

import com.crashnote.core.config.ConfigLoader;
import com.crashnote.external.config.Config;
import com.crashnote.logger.config.LoggerConfigFactory;

/**
 * Factory to create instance(s) of {@link WebConfig}.
 * Loads additional configuration settings from file 'crashnote.web.conf'.
 */
public class WebConfigFactory<C extends WebConfig>
    extends LoggerConfigFactory<C> {

    // SETUP ======================================================================================

    public WebConfigFactory() {
        super();
    }

    public WebConfigFactory(final ConfigLoader loader) {
        super(loader);
    }


    // SHARED =====================================================================================

    @Override
    public C create() {
        @SuppressWarnings("unchecked")
        final C result = (C) new WebConfig(readConf());
        return result;
    }

    @Override
    protected Config readDefaultFileConf() {
        return
            loader.fromFile("crashnote.web")                // #1 web default props
                .withFallback(super.readDefaultFileConf()); // #2 other default props
    }
}
