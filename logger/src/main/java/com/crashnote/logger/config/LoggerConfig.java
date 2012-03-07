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
package com.crashnote.logger.config;

import com.crashnote.core.config.Config;
import com.crashnote.core.log.LogLog;
import com.crashnote.core.model.types.ApplicationType;
import com.crashnote.core.report.Reporter;
import com.crashnote.logger.helper.AutoLogConnector;
import com.crashnote.logger.report.LoggerReporter;

/**
 * Customized {@link Config} for dealing with log frameworks.
 */
public class LoggerConfig<C extends LoggerConfig<C>>
    extends Config<C> {

    public static final String PCKG_BASE = "com.crashnote";

    // SETUP ======================================================================================

    public LoggerConfig() {
        super();
    }

    @Override
    public void initDefaults() {
        super.initDefaults();

        // assume this is a server application
        setAppType(ApplicationType.SERVER);
    }

    // FACTORY ====================================================================================

    @Override
    public LoggerReporter<C> getReporter() {
        return new LoggerReporter(this);
    }

    public AutoLogConnector getLogConnector(final LoggerReporter reporter) {
        return new AutoLogConnector(this, reporter);
    }
}
