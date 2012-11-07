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
package com.crashnote.logback.impl;

import com.crashnote.logger.config.LoggerConfig;
import com.crashnote.logger.helper.LogConnector;
import com.crashnote.logger.report.LoggerReporter;
import com.crashnote.logback.CrashAppender;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@link LogConnector} for the 'Logback' library:
 * Attaches the {@link CrashAppender} to Logback.
 */
public class LogbackConnector
        extends LogConnector<LoggerConfig, CrashAppender> {

    // VARS =======================================================================================

    private static final Logger log = CrashAppender.getTargetLogger(LogbackConnector.class);


    // INTERFACE ==================================================================================

    @Override
    public void err(final String msg, final Throwable th) {
        log.error(msg, th);
    }

    @Override
    public void debug(final String msg) {
        log.debug(msg);
    }


    // SHARED =====================================================================================

    @Override
    protected void attach(final LoggerConfig config, final LoggerReporter reporter) {

        // initialize
        final Logger rootLogger = (Logger) LoggerFactory.getLogger("ROOT");

        // attach
        myAppender = new CrashAppender(config, reporter);
        myAppender.setContext(rootLogger.getLoggerContext());
        rootLogger.addAppender(myAppender);
    }

    @Override
    protected void detach() {
        final Logger rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
        rootLogger.detachAppender(myAppender);
    }
}
