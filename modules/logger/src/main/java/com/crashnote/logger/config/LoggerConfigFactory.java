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
package com.crashnote.logger.config;

import com.crashnote.core.config.ConfigLoader;
import com.crashnote.core.config.CrashConfigFactory;

public class LoggerConfigFactory<C extends LoggerConfig>
        extends CrashConfigFactory<C> {

    // SETUP ======================================================================================

    public LoggerConfigFactory() {
        super();
    }

    public LoggerConfigFactory(final ConfigLoader loader) {
        super(loader);
    }

    // SHARED ===================================================================================

    @Override
    protected C create() {
        @SuppressWarnings("unchecked")
        final C result = (C) new LoggerConfig(readConf());
        return result;
    }

}