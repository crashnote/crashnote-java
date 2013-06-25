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
package com.crashnote.web;

import com.crashnote.logger.config.LoggerConfig;
import com.crashnote.logger.helper.AutoLogConnector;
import com.crashnote.logger.report.LoggerReporter;

/**
 * A system to capture exceptions in a web application with a lifecycle.
 * Contains a reporter to send the errors and a connector to hook into a logging framework.
 */
public class CrashSystem<C extends LoggerConfig, R extends LoggerReporter> {

    // VARS =======================================================================================

    private R reporter;

    private boolean started = false;

    private AutoLogConnector connector;


    // INTERFACE ==================================================================================

    public boolean start(final C config) {

        // if the configuration enables Crashnote ...
        if (!started && config.isEnabled()) {

            // ... create the central reporter service
            reporter = (R) config.getReporter();
            reporter.start();

            // ... and install the log appender(s)
            connector = config.getLogConnector(reporter);
            connector.start();

            started = true;
        }

        return started;
    }

    public void stop() {

        // disconnect the appenders
        if (connector != null) connector.stop();

        // close the reporter (and let it quickly flush cached data)
        if (reporter != null) reporter.stop();

        started = false;
    }


    // GET ========================================================================================

    public R getReporter() {
        return reporter;
    }

    public boolean isStarted() {
        return started;
    }
}
